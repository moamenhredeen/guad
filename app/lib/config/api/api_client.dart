import 'package:dio/dio.dart';

import 'package:guad/domain/core/app_exceptions.dart';

class ApiClient {
  final Dio _dio;

  ApiClient({required String baseUrl, required Interceptor authInterceptor})
    : _dio = _buildDio(baseUrl, authInterceptor);

  Dio get dio => _dio;

  static Dio _buildDio(String baseUrl, Interceptor authInterceptor) {
    final dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 30),
        receiveTimeout: const Duration(seconds: 30),
        headers: {'Content-Type': 'application/json'},
      ),
    );

    dio.interceptors.addAll([authInterceptor, _LogInterceptor()]);

    return dio;
  }
}

class _LogInterceptor extends Interceptor {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    AppLogger.debug('→ ${options.method} ${options.path}');
    handler.next(options);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    AppLogger.info('← ${response.statusCode} ${response.requestOptions.path}');
    handler.next(response);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    AppLogger.error(
      '✗ ${err.response?.statusCode} ${err.requestOptions.path}: ${err.message}',
    );
    handler.next(err);
  }
}
