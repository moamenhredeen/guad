enum AppEnvironment { dev, prod }

class EnvConfig {
  final AppEnvironment environment;
  final String apiBaseUrl;
  final String keycloakIssuer;
  final String keycloakClientId;
  final String keycloakRedirectUrl;
  final String keycloakPostLogoutRedirectUrl;
  final bool allowInsecureAuthConnections;
  final String appName;

  const EnvConfig({
    required this.environment,
    required this.apiBaseUrl,
    required this.keycloakIssuer,
    required this.keycloakClientId,
    required this.keycloakRedirectUrl,
    required this.keycloakPostLogoutRedirectUrl,
    required this.allowInsecureAuthConnections,
    required this.appName,
  });

  static const dev = EnvConfig(
    environment: AppEnvironment.dev,
    apiBaseUrl: String.fromEnvironment(
      'API_BASE_URL',
      defaultValue: 'http://192.168.178.86:8080/api',
    ),
    keycloakIssuer: String.fromEnvironment(
      'KEYCLOAK_ISSUER',
      defaultValue: 'http://192.168.178.86:8081/realms/guad-app',
    ),
    keycloakClientId: String.fromEnvironment(
      'KEYCLOAK_CLIENT_ID',
      defaultValue: 'guad-mobile',
    ),
    keycloakRedirectUrl: String.fromEnvironment(
      'KEYCLOAK_REDIRECT_URL',
      defaultValue: 'com.guad.app:/oauth2redirect',
    ),
    keycloakPostLogoutRedirectUrl: String.fromEnvironment(
      'KEYCLOAK_POST_LOGOUT_REDIRECT_URL',
      defaultValue: 'com.guad.app:/oauth2redirect',
    ),
    allowInsecureAuthConnections: bool.fromEnvironment(
      'ALLOW_INSECURE_AUTH_CONNECTIONS',
      defaultValue: true,
    ),
    appName: 'Guad (Dev)',
  );

  static const prod = EnvConfig(
    environment: AppEnvironment.prod,
    apiBaseUrl: String.fromEnvironment(
      'API_BASE_URL',
      defaultValue: 'https://api.guad.app/api',
    ),
    keycloakIssuer: String.fromEnvironment(
      'KEYCLOAK_ISSUER',
      defaultValue: 'https://auth.guad.app/realms/guad-app',
    ),
    keycloakClientId: String.fromEnvironment(
      'KEYCLOAK_CLIENT_ID',
      defaultValue: 'guad-mobile',
    ),
    keycloakRedirectUrl: String.fromEnvironment(
      'KEYCLOAK_REDIRECT_URL',
      defaultValue: 'com.guad.app:/oauth2redirect',
    ),
    keycloakPostLogoutRedirectUrl: String.fromEnvironment(
      'KEYCLOAK_POST_LOGOUT_REDIRECT_URL',
      defaultValue: 'com.guad.app:/oauth2redirect',
    ),
    allowInsecureAuthConnections: bool.fromEnvironment(
      'ALLOW_INSECURE_AUTH_CONNECTIONS',
    ),
    appName: 'Guad',
  );

  bool get isDev => environment == AppEnvironment.dev;
  bool get isProd => environment == AppEnvironment.prod;
}
