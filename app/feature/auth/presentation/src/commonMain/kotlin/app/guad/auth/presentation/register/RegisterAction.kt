package app.guad.auth.presentation.register

sealed interface RegisterAction {
    data object OnLoginClick: RegisterAction
    data object OnRegisterClick: RegisterAction
    data object OnTogglePasswordVisibilityClick: RegisterAction
    data object OnInputTextFocusGain: RegisterAction
}