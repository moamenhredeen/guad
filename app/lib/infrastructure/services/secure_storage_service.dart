import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:fresh/fresh.dart';

import 'package:guad/domain/core/app_exceptions.dart';

class AuthToken {
  final String accessToken;
  final String refreshToken;
  final DateTime expiresAt;
  final DateTime refreshTokenExpiresAt;

  const AuthToken({
    required this.accessToken,
    required this.refreshToken,
    required this.expiresAt,
    required this.refreshTokenExpiresAt,
  });

  bool get isExpired => DateTime.now().isAfter(expiresAt);
  bool get isRefreshExpired => DateTime.now().isAfter(refreshTokenExpiresAt);

  Map<String, dynamic> toJson() => {
    'accessToken': accessToken,
    'refreshToken': refreshToken,
    'expiresAt': expiresAt.toIso8601String(),
    'refreshTokenExpiresAt': refreshTokenExpiresAt.toIso8601String(),
  };

  factory AuthToken.fromJson(Map<String, dynamic> json) => AuthToken(
    accessToken: json['accessToken'] as String,
    refreshToken: json['refreshToken'] as String,
    expiresAt: DateTime.parse(json['expiresAt'] as String),
    refreshTokenExpiresAt: DateTime.parse(
      json['refreshTokenExpiresAt'] as String,
    ),
  );
}

class SecureStorageService extends TokenStorage<AuthToken> {
  final _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
  );

  static const _tokenKey = 'auth_token';
  static const _biometricKey = 'biometric_enabled';

  @override
  Future<AuthToken?> read() async {
    try {
      final raw = await _storage.read(key: _tokenKey);
      if (raw == null) return null;
      return AuthToken.fromJson(jsonDecode(raw) as Map<String, dynamic>);
    } catch (e) {
      throw StorageException(e.toString());
    }
  }

  @override
  Future<void> write(AuthToken token) async {
    try {
      await _storage.write(key: _tokenKey, value: jsonEncode(token.toJson()));
    } catch (e) {
      throw StorageException(e.toString());
    }
  }

  @override
  Future<void> delete() async {
    await _storage.delete(key: _tokenKey);
  }

  Future<bool> isTokenExpired() async {
    final token = await read();
    return token?.isExpired ?? true;
  }

  Future<bool> isRefreshTokenExpired() async {
    final token = await read();
    return token?.isRefreshExpired ?? true;
  }

  Future<bool> getBiometricEnabled() async {
    final val = await _storage.read(key: _biometricKey);
    return val == 'true';
  }

  Future<void> setBiometricEnabled() async {
    await _storage.write(key: _biometricKey, value: 'true');
  }

  Future<void> deleteBiometricEnabled() async {
    await _storage.delete(key: _biometricKey);
  }
}
