import 'package:guad/features/auth/data/datasources/keycloak_auth_datasource.dart';
import 'package:guad/features/auth/data/datasources/secure_token_storage.dart';
import 'package:guad/features/auth/domain/entities/app_user.dart';
import 'package:guad/features/auth/domain/entities/auth_session.dart';
import 'package:guad/features/auth/domain/entities/auth_token.dart';
import 'package:guad/features/auth/domain/repositories/auth_repository.dart';
import 'package:guad/infrastructure/services/key_value_storage_service.dart';

class KeycloakAuthRepository implements AuthRepository {
  const KeycloakAuthRepository({
    required this.authDataSource,
    required this.tokenStorage,
    required this.keyValueStorage,
  });

  static const _userKey = 'auth_user';
  static const _biometricEnabledKey = 'biometric_enabled';

  final KeycloakAuthDataSource authDataSource;
  final SecureTokenStorage tokenStorage;
  final KeyValueStorageService keyValueStorage;

  @override
  Future<AuthSession?> readSession() async {
    final user = AppUser.tryDecode(keyValueStorage.getString(_userKey));
    final token = await tokenStorage.read();

    if (user == null || token == null || token.isRefreshExpired) {
      if (token?.isRefreshExpired == true) {
        await clearSession();
      }
      return null;
    }

    return AuthSession(token: token, user: user);
  }

  @override
  Future<AuthSession> signIn() => authDataSource.signIn();

  @override
  Future<void> signOut(AuthToken? token) => authDataSource.signOut(token);

  @override
  Future<void> saveSession(AuthSession session) async {
    await tokenStorage.write(session.token);
    await keyValueStorage.setString(_userKey, session.user.encode());
  }

  @override
  Future<void> clearSession() async {
    await tokenStorage.delete();
    await keyValueStorage.remove(_userKey);
  }

  @override
  Future<void> updateUser(AppUser user) {
    return keyValueStorage.setString(_userKey, user.encode());
  }

  @override
  bool getBiometricEnabled() {
    return keyValueStorage.getBool(_biometricEnabledKey) ?? false;
  }

  @override
  Future<void> setBiometricEnabled({required bool value}) {
    return keyValueStorage.setBool(_biometricEnabledKey, value: value);
  }
}
