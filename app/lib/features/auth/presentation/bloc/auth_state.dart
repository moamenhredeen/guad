part of 'auth_bloc.dart';

enum AuthStatus {
  initial,
  loading,
  authenticated,
  unauthenticated,
  biometricLocked,
}

class AuthState extends Equatable {
  const AuthState({
    this.status = AuthStatus.initial,
    this.user,
    this.isLoading = false,
    this.errorMessage,
    this.biometricAvailable = false,
    this.biometricEnabled = false,
  });

  final AuthStatus status;
  final AppUser? user;
  final bool isLoading;
  final String? errorMessage;
  final bool biometricAvailable;
  final bool biometricEnabled;

  bool get isAuthenticated => status == AuthStatus.authenticated;
  bool get requiresBiometricAuth => status == AuthStatus.biometricLocked;

  AuthState copyWith({
    AuthStatus? status,
    AppUser? user,
    bool clearUser = false,
    bool? isLoading,
    String? errorMessage,
    bool clearError = false,
    bool? biometricAvailable,
    bool? biometricEnabled,
  }) {
    return AuthState(
      status: status ?? this.status,
      user: clearUser ? null : user ?? this.user,
      isLoading: isLoading ?? this.isLoading,
      errorMessage: clearError ? null : errorMessage ?? this.errorMessage,
      biometricAvailable: biometricAvailable ?? this.biometricAvailable,
      biometricEnabled: biometricEnabled ?? this.biometricEnabled,
    );
  }

  @override
  List<Object?> get props => [
    status,
    user,
    isLoading,
    errorMessage,
    biometricAvailable,
    biometricEnabled,
  ];
}
