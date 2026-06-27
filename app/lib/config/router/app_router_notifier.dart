import 'dart:async';
import 'package:flutter/foundation.dart';

import 'package:guad/features/auth/presentation/bloc/auth_bloc.dart';
import 'package:guad/infrastructure/services/connectivity_service.dart';

class AppRouterNotifier extends ChangeNotifier {
  AuthStatus authStatus = AuthStatus.initial;
  bool hasInternet = true;

  late final StreamSubscription<AuthState> _authSub;
  late final StreamSubscription<bool> _connectivitySub;

  AppRouterNotifier({
    required AuthBloc authBloc,
    required ConnectivityService connectivityService,
  }) : authStatus = authBloc.state.status {
    _authSub = authBloc.stream.listen((state) {
      authStatus = state.status;
      notifyListeners();
    });

    _connectivitySub = connectivityService.onConnectivityChanged.listen((
      connected,
    ) {
      hasInternet = connected;
      notifyListeners();
    });
  }

  bool get isAuthenticated =>
      authStatus == AuthStatus.authenticated ||
      authStatus == AuthStatus.biometricLocked;
  bool get isInitializing =>
      authStatus == AuthStatus.initial || authStatus == AuthStatus.loading;

  @override
  void dispose() {
    _authSub.cancel();
    _connectivitySub.cancel();
    super.dispose();
  }
}
