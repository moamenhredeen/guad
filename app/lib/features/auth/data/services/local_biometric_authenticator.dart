import 'package:local_auth/local_auth.dart';

import 'package:guad/features/auth/domain/repositories/biometric_authenticator.dart';

class LocalBiometricAuthenticator implements BiometricAuthenticator {
  LocalBiometricAuthenticator({LocalAuthentication? localAuthentication})
    : _localAuthentication = localAuthentication ?? LocalAuthentication();

  final LocalAuthentication _localAuthentication;

  @override
  Future<bool> isAvailable() => _localAuthentication.canCheckBiometrics;

  @override
  Future<bool> authenticate(String reason) => _localAuthentication.authenticate(
    localizedReason: reason,
    options: const AuthenticationOptions(biometricOnly: true),
  );
}
