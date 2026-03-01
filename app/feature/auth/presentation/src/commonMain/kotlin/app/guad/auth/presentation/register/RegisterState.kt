package app.guad.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import app.guad.core.presentation.util.UiText

data class RegisterState (
    val emailTextState: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val emailError: UiText? = null,

    val passwordTextState: TextFieldState = TextFieldState(),
    val isPasswordValid: Boolean = false,
    val passwordError: UiText? = null,
    val isPasswordVisible: Boolean = false,

    val usernameTextState: TextFieldState = TextFieldState(),
    val isUsernameValid: Boolean = false,
    val usernameError: UiText? = null,

    val registrationError: UiText? = null,
    val isRegistrationLoading: Boolean = false,
    val canRegister: Boolean = false,
)