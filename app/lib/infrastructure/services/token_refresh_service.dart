import 'package:fresh_dio/fresh_dio.dart';

import 'package:guad/infrastructure/services/keycloak_auth_service.dart';
import 'package:guad/infrastructure/services/secure_storage_service.dart';

class TokenRefreshService {
  late final Fresh<AuthToken> fresh;
  final KeycloakAuthService keycloakAuth;

  TokenRefreshService({
    required SecureStorageService secureStorage,
    required this.keycloakAuth,
  }) {
    fresh = Fresh<AuthToken>(
      tokenStorage: secureStorage,
      refreshToken: (token, _) => keycloakAuth.refresh(token),
      tokenHeader: (token) => {'Authorization': 'Bearer ${token.accessToken}'},
      shouldRefresh: (response) => response?.statusCode == 401,
    );
  }

  Stream<AuthenticationStatus> get authStatusStream =>
      fresh.authenticationStatus;

  Future<void> setToken(AuthToken token) => fresh.setToken(token);
  Future<void> clearToken() => fresh.setToken(null);
}
