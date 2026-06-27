enum ErrorCode {
  network,
  unauthorized,
  notFound,
  server,
  mapping,
  storage,
  biometrics,
  unknown,
}

class AppException implements Exception {
  final String message;
  final String? details;
  final ErrorCode errorCode;

  AppException(
    this.message, {
    this.details,
    this.errorCode = ErrorCode.unknown,
  }) {
    AppLogger.error(
      '[$errorCode] $message${details != null ? ' — $details' : ''}',
    );
  }

  @override
  String toString() => 'AppException($errorCode): $message';
}

class NetworkException extends AppException {
  NetworkException([String? details])
    : super('Network error', details: details, errorCode: ErrorCode.network);
}

class UnauthorizedException extends AppException {
  UnauthorizedException([String? details])
    : super(
        'Unauthorized',
        details: details,
        errorCode: ErrorCode.unauthorized,
      );
}

class AuthCancelledException extends AppException {
  AuthCancelledException([String? details])
    : super(
        'Authentication cancelled',
        details: details,
        errorCode: ErrorCode.unauthorized,
      );
}

class ServerException extends AppException {
  final int? statusCode;
  ServerException({this.statusCode, String? details})
    : super(
        'Server error ${statusCode ?? ''}',
        details: details,
        errorCode: ErrorCode.server,
      );
}

class StorageException extends AppException {
  StorageException([String? details])
    : super('Storage error', details: details, errorCode: ErrorCode.storage);
}

class BiometricsException extends AppException {
  BiometricsException([String? details])
    : super(
        'Biometric error',
        details: details,
        errorCode: ErrorCode.biometrics,
      );
}

// Avoid circular import — logger is inlined here
class AppLogger {
  static void error(String msg) => _log('🚨 $msg');
  static void info(String msg) => _log('ℹ️  $msg');
  static void debug(String msg) => _log('🔍 $msg');

  // ignore: avoid_print
  static void _log(String msg) => print(msg);
}
