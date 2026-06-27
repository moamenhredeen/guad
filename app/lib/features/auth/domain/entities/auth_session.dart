import 'package:guad/features/auth/domain/entities/app_user.dart';
import 'package:guad/features/auth/domain/entities/auth_token.dart';

class AuthSession {
  const AuthSession({required this.token, required this.user});

  final AuthToken token;
  final AppUser user;
}
