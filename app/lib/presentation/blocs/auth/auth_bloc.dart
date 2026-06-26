import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/infrastructure/services/biometric_service.dart';
import 'package:guad/infrastructure/services/key_value_storage_service.dart';
import 'package:guad/presentation/blocs/auth/app_user.dart';

part 'auth_event.dart';
part 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  AuthBloc({required this.keyValueStorage, required this.biometricService})
    : super(const AuthState()) {
    on<AuthStarted>(_onStarted);
    on<AuthLoginSubmitted>(_onLoginSubmitted);
    on<AuthLogoutRequested>(_onLogoutRequested);
    on<AuthBiometricLoginRequested>(_onBiometricLoginRequested);
    on<AuthBiometricEnabled>(_onBiometricEnabled);
    on<AuthBiometricDisabled>(_onBiometricDisabled);
    on<AuthUserUpdated>(_onUserUpdated);
    on<AuthErrorCleared>(_onErrorCleared);

    add(const AuthStarted());
  }

  static const _userKey = 'auth_user';
  static const _biometricEnabledKey = 'biometric_enabled';

  final KeyValueStorageService keyValueStorage;
  final BiometricService biometricService;

  Future<void> _onStarted(AuthStarted event, Emitter<AuthState> emit) async {
    emit(state.copyWith(status: AuthStatus.loading, clearError: true));

    final biometricAvailable = await _isBiometricAvailable();
    final biometricEnabled =
        keyValueStorage.getBool(_biometricEnabledKey) ?? false;
    final user = AppUser.tryDecode(keyValueStorage.getString(_userKey));

    if (user == null) {
      emit(
        AuthState(
          status: AuthStatus.unauthenticated,
          biometricAvailable: biometricAvailable,
          biometricEnabled: biometricEnabled,
        ),
      );
      return;
    }

    emit(
      AuthState(
        status: biometricAvailable && biometricEnabled
            ? AuthStatus.biometricLocked
            : AuthStatus.authenticated,
        user: user,
        biometricAvailable: biometricAvailable,
        biometricEnabled: biometricEnabled,
      ),
    );
  }

  Future<void> _onLoginSubmitted(
    AuthLoginSubmitted event,
    Emitter<AuthState> emit,
  ) async {
    emit(state.copyWith(isLoading: true, clearError: true));

    final name = event.email.split('@').first.trim();
    final user = AppUser(
      id: event.email.toLowerCase(),
      email: event.email,
      firstName: name.isEmpty ? 'Guad' : _capitalize(name),
      lastName: 'User',
    );

    await keyValueStorage.setString(_userKey, user.encode());

    emit(
      state.copyWith(
        status: AuthStatus.authenticated,
        user: user,
        isLoading: false,
      ),
    );
  }

  Future<void> _onLogoutRequested(
    AuthLogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    await keyValueStorage.remove(_userKey);
    emit(
      AuthState(
        status: AuthStatus.unauthenticated,
        biometricAvailable: state.biometricAvailable,
        biometricEnabled: state.biometricEnabled,
      ),
    );
  }

  Future<void> _onBiometricLoginRequested(
    AuthBiometricLoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(state.copyWith(isLoading: true, clearError: true));
    final authenticated = await biometricService.authenticate(
      'Use your biometrics to continue',
    );

    emit(
      state.copyWith(
        status: authenticated ? AuthStatus.authenticated : state.status,
        isLoading: false,
        errorMessage: authenticated ? null : 'Biometric authentication failed.',
        clearError: authenticated,
      ),
    );
  }

  Future<void> _onBiometricEnabled(
    AuthBiometricEnabled event,
    Emitter<AuthState> emit,
  ) async {
    emit(state.copyWith(isLoading: true, clearError: true));

    final authenticated = await biometricService.authenticate(
      'Enable biometric login',
    );
    if (!authenticated) {
      emit(
        state.copyWith(
          isLoading: false,
          errorMessage: 'Biometric authentication failed.',
        ),
      );
      return;
    }

    await keyValueStorage.setBool(_biometricEnabledKey, value: true);
    emit(state.copyWith(isLoading: false, biometricEnabled: true));
  }

  Future<void> _onBiometricDisabled(
    AuthBiometricDisabled event,
    Emitter<AuthState> emit,
  ) async {
    await keyValueStorage.setBool(_biometricEnabledKey, value: false);
    emit(state.copyWith(biometricEnabled: false));
  }

  Future<void> _onUserUpdated(
    AuthUserUpdated event,
    Emitter<AuthState> emit,
  ) async {
    final current = state.user;
    if (current == null) return;

    final updated = current.copyWith(
      firstName: event.firstName,
      lastName: event.lastName,
      phoneNumber: event.phoneNumber,
    );
    await keyValueStorage.setString(_userKey, updated.encode());
    emit(state.copyWith(user: updated));
  }

  void _onErrorCleared(AuthErrorCleared event, Emitter<AuthState> emit) {
    emit(state.copyWith(clearError: true));
  }

  Future<bool> _isBiometricAvailable() async {
    try {
      return biometricService.isAvailable();
    } catch (_) {
      return false;
    }
  }

  static String _capitalize(String value) {
    if (value.isEmpty) return value;
    return value[0].toUpperCase() + value.substring(1);
  }
}
