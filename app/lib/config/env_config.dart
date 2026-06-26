enum AppEnvironment { dev, prod }

class EnvConfig {
  final AppEnvironment environment;
  final String apiBaseUrl;
  final String appName;

  const EnvConfig({
    required this.environment,
    required this.apiBaseUrl,
    required this.appName,
  });

  static const dev = EnvConfig(
    environment: AppEnvironment.dev,
    apiBaseUrl: 'http://192.168.178.86:3000/api/v1',
    appName: 'Guad (Dev)',
  );

  static const prod = EnvConfig(
    environment: AppEnvironment.prod,
    apiBaseUrl: 'https://api.guad.app/api/v1',
    appName: 'Guad',
  );

  bool get isDev => environment == AppEnvironment.dev;
  bool get isProd => environment == AppEnvironment.prod;
}
