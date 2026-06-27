import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import 'package:guad/app/localization/locale_cubit.dart';
import 'package:guad/config/env_config.dart';
import 'package:guad/config/api/api_client.dart';
import 'package:guad/config/router/app_router.dart';
import 'package:guad/config/router/app_router_notifier.dart';
import 'package:guad/config/theme/app_theme.dart';
import 'package:guad/features/auth/data/datasources/keycloak_auth_datasource.dart';
import 'package:guad/features/auth/data/datasources/secure_token_storage.dart';
import 'package:guad/features/auth/data/repositories/keycloak_auth_repository.dart';
import 'package:guad/features/auth/data/services/token_refresh_service.dart';
import 'package:guad/features/auth/domain/repositories/biometric_authenticator.dart';
import 'package:guad/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:guad/features/auth/presentation/screens/biometric_gate_screen.dart';
import 'package:guad/features/gtd/data/datasources/gtd_remote_data_source.dart';
import 'package:guad/features/gtd/data/repositories/api_gtd_repository.dart';
import 'package:guad/features/gtd/domain/repositories/gtd_repository.dart';
import 'package:guad/features/gtd/domain/services/gtd_change_bus.dart';
import 'package:guad/gen/l10n/app_localizations.dart';
import 'package:guad/infrastructure/services/connectivity_service.dart';
import 'package:guad/infrastructure/services/key_value_storage_service.dart';

class App extends StatelessWidget {
  const App({
    required this.config,
    required this.keyValueStorage,
    required this.connectivityService,
    required this.biometricService,
    super.key,
  });

  final EnvConfig config;
  final KeyValueStorageService keyValueStorage;
  final ConnectivityService connectivityService;
  final BiometricAuthenticator biometricService;

  @override
  Widget build(BuildContext context) {
    final tokenStorage = SecureTokenStorage();
    final keycloakAuth = KeycloakAuthDataSource(config: config);
    final authRepository = KeycloakAuthRepository(
      authDataSource: keycloakAuth,
      tokenStorage: tokenStorage,
      keyValueStorage: keyValueStorage,
    );
    final tokenRefresh = TokenRefreshService(
      tokenStorage: tokenStorage,
      keycloakAuth: keycloakAuth,
    );
    final apiClient = ApiClient(
      baseUrl: config.apiBaseUrl,
      authInterceptor: tokenRefresh.fresh,
    );
    final gtdRepository = ApiGtdRepository(GtdRemoteDataSource(apiClient.dio));

    return MultiRepositoryProvider(
      providers: [
        RepositoryProvider.value(value: config),
        RepositoryProvider.value(value: keyValueStorage),
        RepositoryProvider.value(value: connectivityService),
        RepositoryProvider.value(value: biometricService),
        RepositoryProvider.value(value: tokenStorage),
        RepositoryProvider.value(value: keycloakAuth),
        RepositoryProvider.value(value: authRepository),
        RepositoryProvider.value(value: tokenRefresh),
        RepositoryProvider.value(value: apiClient),
        RepositoryProvider<GtdRepository>.value(value: gtdRepository),
        RepositoryProvider(
          create: (_) => GtdChangeBus(),
          dispose: (changeBus) => changeBus.dispose(),
        ),
      ],
      child: MultiBlocProvider(
        providers: [
          BlocProvider(
            create: (_) => AuthBloc(
              authRepository: authRepository,
              biometricAuthenticator: biometricService,
              tokenRefreshService: tokenRefresh,
            ),
          ),
          BlocProvider(
            create: (_) => LocaleCubit(keyValueStorage: keyValueStorage),
          ),
        ],
        child: const _AppView(),
      ),
    );
  }
}

class _AppView extends StatefulWidget {
  const _AppView();

  @override
  State<_AppView> createState() => _AppViewState();
}

class _AppViewState extends State<_AppView> {
  late final AppRouterNotifier _routerNotifier;
  late final GoRouter _router;

  @override
  void initState() {
    super.initState();
    _routerNotifier = AppRouterNotifier(
      authBloc: context.read<AuthBloc>(),
      connectivityService: context.read<ConnectivityService>(),
    );
    _router = createRouter(_routerNotifier);
  }

  @override
  void dispose() {
    _routerNotifier.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      themeMode: ThemeMode.system,
      locale: context.watch<LocaleCubit>().state,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      debugShowCheckedModeBanner: false,
      builder: (context, child) {
        final requiresBio = context.select(
          (AuthBloc bloc) => bloc.state.requiresBiometricAuth,
        );
        if (requiresBio) return const BiometricGateScreen();
        return child ?? const SizedBox.shrink();
      },
    );
  }
}
