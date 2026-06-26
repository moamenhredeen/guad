import 'package:dio/dio.dart';
import 'package:fresh_dio/fresh_dio.dart';

import 'package:guad/domain/core/app_exceptions.dart';
import 'package:guad/infrastructure/services/secure_storage_service.dart';

class TokenRefreshService {
  late final Fresh<AuthToken> fresh;

  TokenRefreshService({required SecureStorageService secureStorage}) {
    fresh = Fresh<AuthToken>(
      tokenStorage: secureStorage,
      refreshToken: _refreshToken,
      tokenHeader: (token) => {'Authorization': 'Bearer ${token.accessToken}'},
      shouldRefresh: (response) => response?.statusCode == 401,
    );
  }

  Stream<AuthenticationStatus> get authStatusStream =>
      fresh.authenticationStatus;

  Future<AuthToken> _refreshToken(AuthToken? token, Dio dio) async {
    try {
      final response = await dio.post(
        '/auth/refresh-token',
        data: {'refresh_token': token?.refreshToken},
      );
      final data =
          (response.data as Map<String, dynamic>)['data']
              as Map<String, dynamic>;
      return AuthToken(
        accessToken: data['token'] as String,
        refreshToken: data['refresh_token'] as String,
        expiresAt: DateTime.now().add(const Duration(hours: 24)),
        refreshTokenExpiresAt: DateTime.now().add(const Duration(days: 30)),
      );
    } on DioException catch (e) {
      throw UnauthorizedException(e.message);
    }
  }

  Future<void> setToken(AuthToken token) => fresh.setToken(token);
  Future<void> clearToken() => fresh.setToken(null);
}
