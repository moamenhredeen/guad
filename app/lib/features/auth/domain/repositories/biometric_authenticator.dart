abstract class BiometricAuthenticator {
  Future<bool> isAvailable();

  Future<bool> authenticate(String reason);
}
