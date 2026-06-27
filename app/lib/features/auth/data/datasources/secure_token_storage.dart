import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:fresh/fresh.dart';

import 'package:guad/domain/core/app_exceptions.dart';
import 'package:guad/features/auth/domain/entities/auth_token.dart';

class SecureTokenStorage extends TokenStorage<AuthToken> {
  SecureTokenStorage({FlutterSecureStorage? storage})
    : _storage =
          storage ??
          const FlutterSecureStorage(
            aOptions: AndroidOptions(encryptedSharedPreferences: true),
          );

  final FlutterSecureStorage _storage;

  static const _tokenKey = 'auth_token';

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
}
