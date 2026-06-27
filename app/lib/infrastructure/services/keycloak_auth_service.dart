import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter_appauth/flutter_appauth.dart';

import 'package:guad/config/env_config.dart';
import 'package:guad/domain/core/app_exceptions.dart';
import 'package:guad/infrastructure/services/secure_storage_service.dart';
import 'package:guad/presentation/blocs/auth/app_user.dart';

class AuthSession {
  const AuthSession({required this.token, required this.user});

  final AuthToken token;
  final AppUser user;
}

class KeycloakAuthService {
  KeycloakAuthService({required this.config, FlutterAppAuth? appAuth})
    : _appAuth = appAuth ?? const FlutterAppAuth();

  final EnvConfig config;
  final FlutterAppAuth _appAuth;

  String get tokenEndpoint =>
      '${config.keycloakIssuer}/protocol/openid-connect/token';

  Future<AuthSession> signIn() async {
    try {
      final result = await _appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          config.keycloakClientId,
          config.keycloakRedirectUrl,
          issuer: config.keycloakIssuer,
          scopes: const ['openid', 'profile', 'email'],
          allowInsecureConnections: config.allowInsecureAuthConnections,
        ),
      );

      final accessToken = result.accessToken;
      final refreshToken = result.refreshToken;
      final idToken = result.idToken;
      if (accessToken == null || refreshToken == null || idToken == null) {
        throw UnauthorizedException(
          'Keycloak did not return a complete token set.',
        );
      }

      final token = AuthToken(
        accessToken: accessToken,
        refreshToken: refreshToken,
        idToken: idToken,
        expiresAt:
            result.accessTokenExpirationDateTime ?? _jwtExpiresAt(accessToken),
        refreshTokenExpiresAt: _jwtExpiresAt(refreshToken, fallbackDays: 30),
      );

      return AuthSession(token: token, user: _userFromIdToken(idToken));
    } on FlutterAppAuthUserCancelledException catch (e) {
      throw AuthCancelledException(e.platformErrorDetails.errorDescription);
    } catch (e) {
      if (e is AppException) rethrow;
      throw UnauthorizedException(e.toString());
    }
  }

  Future<AuthToken> refresh(AuthToken? token) async {
    final refreshToken = token?.refreshToken;
    if (refreshToken == null || refreshToken.isEmpty) {
      throw UnauthorizedException('Missing refresh token.');
    }

    try {
      final response = await Dio().post<Map<String, dynamic>>(
        tokenEndpoint,
        data: {
          'grant_type': 'refresh_token',
          'client_id': config.keycloakClientId,
          'refresh_token': refreshToken,
        },
        options: Options(contentType: Headers.formUrlEncodedContentType),
      );

      final data = response.data;
      if (data == null) {
        throw UnauthorizedException(
          'Keycloak returned an empty refresh response.',
        );
      }

      final accessToken = data['access_token'] as String?;
      final refreshedToken = data['refresh_token'] as String? ?? refreshToken;
      if (accessToken == null) {
        throw UnauthorizedException('Keycloak did not return an access token.');
      }

      final idToken = data['id_token'] as String? ?? token?.idToken;
      return AuthToken(
        accessToken: accessToken,
        refreshToken: refreshedToken,
        idToken: idToken,
        expiresAt: _expiresAt(data['expires_in'], accessToken),
        refreshTokenExpiresAt: _expiresAt(
          data['refresh_expires_in'],
          refreshedToken,
          fallbackDays: 30,
        ),
      );
    } on DioException catch (e) {
      throw UnauthorizedException(e.message);
    }
  }

  Future<void> signOut(AuthToken? token) async {
    final idToken = token?.idToken;
    if (idToken == null || idToken.isEmpty) return;

    try {
      await _appAuth.endSession(
        EndSessionRequest(
          idTokenHint: idToken,
          postLogoutRedirectUrl: config.keycloakPostLogoutRedirectUrl,
          issuer: config.keycloakIssuer,
          allowInsecureConnections: config.allowInsecureAuthConnections,
        ),
      );
    } catch (_) {
      // Local logout should still succeed if the browser session is already gone.
    }
  }

  DateTime _expiresAt(dynamic seconds, String jwt, {int fallbackDays = 0}) {
    if (seconds is int) {
      return DateTime.now().add(Duration(seconds: seconds));
    }
    return _jwtExpiresAt(jwt, fallbackDays: fallbackDays);
  }

  DateTime _jwtExpiresAt(String jwt, {int fallbackDays = 0}) {
    final payload = _jwtPayload(jwt);
    final exp = payload['exp'];
    if (exp is int) {
      return DateTime.fromMillisecondsSinceEpoch(exp * 1000);
    }
    return DateTime.now().add(Duration(days: fallbackDays));
  }

  AppUser _userFromIdToken(String idToken) {
    final claims = _jwtPayload(idToken);
    final email = claims['email'] as String? ?? '';
    final givenName = claims['given_name'] as String?;
    final familyName = claims['family_name'] as String?;
    final preferredUsername = claims['preferred_username'] as String?;
    final displayName = claims['name'] as String? ?? preferredUsername ?? email;
    final names = displayName.trim().split(RegExp(r'\s+'));

    return AppUser(
      id: claims['sub'] as String? ?? email,
      email: email,
      firstName: givenName ?? (names.isEmpty ? 'Guad' : names.first),
      lastName: familyName ?? (names.length > 1 ? names.skip(1).join(' ') : ''),
    );
  }

  Map<String, dynamic> _jwtPayload(String jwt) {
    final parts = jwt.split('.');
    if (parts.length < 2) {
      throw UnauthorizedException('Invalid Keycloak token.');
    }
    final payload = utf8.decode(
      base64Url.decode(base64Url.normalize(parts[1])),
    );
    return jsonDecode(payload) as Map<String, dynamic>;
  }
}
