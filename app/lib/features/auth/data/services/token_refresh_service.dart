import 'package:fresh_dio/fresh_dio.dart';

import 'package:guad/features/auth/data/datasources/keycloak_auth_datasource.dart';
import 'package:guad/features/auth/data/datasources/secure_token_storage.dart';
import 'package:guad/features/auth/domain/entities/auth_token.dart';

class TokenRefreshService {
  TokenRefreshService({
    required SecureTokenStorage tokenStorage,
    required this.keycloakAuth,
  }) {
    fresh = Fresh<AuthToken>(
      tokenStorage: tokenStorage,
      refreshToken: (token, _) => keycloakAuth.refresh(token),
      tokenHeader: (token) => {'Authorization': 'Bearer ${token.accessToken}'},
      shouldRefresh: (response) => response?.statusCode == 401,
    );
  }

  late final Fresh<AuthToken> fresh;
  final KeycloakAuthDataSource keycloakAuth;

  Stream<AuthenticationStatus> get authStatusStream =>
      fresh.authenticationStatus;

  Future<void> setToken(AuthToken token) => fresh.setToken(token);
  Future<void> clearToken() => fresh.setToken(null);
}
