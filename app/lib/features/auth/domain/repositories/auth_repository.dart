import 'package:guad/features/auth/domain/entities/app_user.dart';
import 'package:guad/features/auth/domain/entities/auth_session.dart';
import 'package:guad/features/auth/domain/entities/auth_token.dart';

abstract class AuthRepository {
  Future<AuthSession?> readSession();

  Future<AuthSession> signIn();

  Future<void> signOut(AuthToken? token);

  Future<void> saveSession(AuthSession session);

  Future<void> clearSession();

  Future<void> updateUser(AppUser user);

  bool getBiometricEnabled();

  Future<void> setBiometricEnabled({required bool value});
}
