package app.guad.auth.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.guad.core.designsystem.components.brand.GuadLogo
import app.guad.core.designsystem.components.buttons.GuadButton
import app.guad.core.designsystem.components.buttons.GuadButtonStyle
import app.guad.core.designsystem.components.textfields.GuadSecureTextField
import app.guad.core.designsystem.components.textfields.GuadTextField
import app.guad.core.designsystem.layouts.GuadAdaptiveFormLayout
import app.guad.core.designsystem.layouts.GuadSnackbarScaffold
import app.guad.core.designsystem.theme.GuadTheme
import guad.feature.auth.presentation.generated.resources.Res
import guad.feature.auth.presentation.generated.resources.email
import guad.feature.auth.presentation.generated.resources.email_placeholder
import guad.feature.auth.presentation.generated.resources.login
import guad.feature.auth.presentation.generated.resources.password
import guad.feature.auth.presentation.generated.resources.password_hint
import guad.feature.auth.presentation.generated.resources.register
import guad.feature.auth.presentation.generated.resources.username
import guad.feature.auth.presentation.generated.resources.username_hint
import guad.feature.auth.presentation.generated.resources.username_placeholder
import guad.feature.auth.presentation.generated.resources.welcome_to_guad
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun RegisterScreenRoot(
    viewModel: RegisterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}


@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    GuadSnackbarScaffold(
        snackbarHostState = snackbarHostState,
    ) {
        GuadAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_to_guad),
            errorText = state.registrationError?.asString(),
            logo = {
                GuadLogo()
            },
            modifier = Modifier.padding(16.dp)

        ) {
            GuadTextField(
                state = state.usernameTextState,
                placeholder = stringResource(Res.string.username_placeholder),
                title = stringResource(Res.string.username),
                supportingText = state.usernameError?.asString() ?: stringResource(Res.string.username_hint),
                isError = state.usernameError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuadTextField(
                state = state.emailTextState,
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                supportingText = state.emailError?.asString(),
                isError = state.emailError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuadSecureTextField(
                state = state.passwordTextState,
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = state.passwordError?.asString() ?: stringResource(Res.string.password_hint),
                isError = state.passwordError != null,
                isPasswordVisible = state.isPasswordVisible,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                },
                onToggleVisibilityClick = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuadButton(
                text = stringResource(Res.string.register),
                style = GuadButtonStyle.PRIMARY,
                enabled = state.canRegister,
                isLoading = state.isRegistrationLoading,
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            GuadButton(
                text = stringResource(Res.string.login),
                style = GuadButtonStyle.SECONDARY,
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                },
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}


@Preview
@Composable
private fun RegisterScreenLightPreview() {
    GuadTheme(darkTheme = false) {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview
@Composable
private fun RegisterScreenDarkPreview() {
    GuadTheme(darkTheme = true) {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
