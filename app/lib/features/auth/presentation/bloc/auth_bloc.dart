import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'package:guad/domain/core/app_exceptions.dart';
import 'package:guad/features/auth/domain/entities/app_user.dart';
import 'package:guad/features/auth/domain/repositories/auth_repository.dart';
import 'package:guad/features/auth/domain/repositories/biometric_authenticator.dart';

part 'auth_event.dart';
part 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  AuthBloc({required this.authRepository, required this.biometricAuthenticator})
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

  final AuthRepository authRepository;
  final BiometricAuthenticator biometricAuthenticator;

  Future<void> _onStarted(AuthStarted event, Emitter<AuthState> emit) async {
    emit(state.copyWith(status: AuthStatus.loading, clearError: true));

    final biometricAvailable = await _isBiometricAvailable();
    final biometricEnabled = authRepository.getBiometricEnabled();
    final session = await authRepository.readSession();

    if (session == null) {
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
        user: session.user,
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

    try {
      final session = await authRepository.signIn();
      await authRepository.saveSession(session);

      emit(
        state.copyWith(
          status: AuthStatus.authenticated,
          user: session.user,
          isLoading: false,
        ),
      );
    } on AuthCancelledException {
      emit(
        state.copyWith(
          status: AuthStatus.unauthenticated,
          isLoading: false,
          errorMessage: 'Sign in was not completed.',
        ),
      );
    } catch (_) {
      emit(
        state.copyWith(
          status: AuthStatus.unauthenticated,
          isLoading: false,
          errorMessage: 'Sign in failed. Please try again.',
        ),
      );
    }
  }

  Future<void> _onLogoutRequested(
    AuthLogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    final session = await authRepository.readSession();
    await authRepository.signOut(session?.token);
    await authRepository.clearSession();
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
    final authenticated = await biometricAuthenticator.authenticate(
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

    final authenticated = await biometricAuthenticator.authenticate(
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

    await authRepository.setBiometricEnabled(value: true);
    emit(state.copyWith(isLoading: false, biometricEnabled: true));
  }

  Future<void> _onBiometricDisabled(
    AuthBiometricDisabled event,
    Emitter<AuthState> emit,
  ) async {
    await authRepository.setBiometricEnabled(value: false);
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
    await authRepository.updateUser(updated);
    emit(state.copyWith(user: updated));
  }

  void _onErrorCleared(AuthErrorCleared event, Emitter<AuthState> emit) {
    emit(state.copyWith(clearError: true));
  }

  Future<bool> _isBiometricAvailable() async {
    try {
      return biometricAuthenticator.isAvailable();
    } catch (_) {
      return false;
    }
  }
}
