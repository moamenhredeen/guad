part of 'auth_bloc.dart';

sealed class AuthEvent extends Equatable {
  const AuthEvent();

  @override
  List<Object?> get props => [];
}

class AuthStarted extends AuthEvent {
  const AuthStarted();
}

class AuthLoginSubmitted extends AuthEvent {
  const AuthLoginSubmitted();
}

class AuthLogoutRequested extends AuthEvent {
  const AuthLogoutRequested();
}

class AuthBiometricLoginRequested extends AuthEvent {
  const AuthBiometricLoginRequested();
}

class AuthBiometricEnabled extends AuthEvent {
  const AuthBiometricEnabled();
}

class AuthBiometricDisabled extends AuthEvent {
  const AuthBiometricDisabled();
}

class AuthUserUpdated extends AuthEvent {
  const AuthUserUpdated({this.firstName, this.lastName, this.phoneNumber});

  final String? firstName;
  final String? lastName;
  final String? phoneNumber;

  @override
  List<Object?> get props => [firstName, lastName, phoneNumber];
}

class AuthErrorCleared extends AuthEvent {
  const AuthErrorCleared();
}
