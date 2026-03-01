package app.guad.core.designsystem.components.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.theme.GuadTheme
import app.guad.core.designsystem.theme.extended

@Composable
fun GuadSecureTextField(
    state: TextFieldState,
    enabled: Boolean = true,
    title: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    onToggleVisibilityClick: () -> Unit,
    isPasswordVisible: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        onFocusChanged(isFocused)
    }

    Column(
        modifier = modifier
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        BasicSecureTextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when {
                        isFocused -> MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.05f
                        )

                        enabled -> MaterialTheme.colorScheme.surface
                        else -> MaterialTheme.colorScheme.extended.secondaryFill
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> MaterialTheme.colorScheme.error
                        isFocused -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            enabled = enabled,
            textObfuscationMode = if (isPasswordVisible) {
                TextObfuscationMode.Visible
            } else TextObfuscationMode.Hidden,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.extended.textPlaceholder
                }
            ),
            interactionSource = interactionSource,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorator = { innerBox ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.text.isEmpty() && placeholder != null) {
                            Text(
                                text = placeholder,
                                color = MaterialTheme.colorScheme.extended.textPlaceholder,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerBox()
                    }

                    Icon(
                        // TODO: use android resources to load the eye svg icon
                        imageVector = if (isPasswordVisible) {
                            Icons.Outlined.Lock
                        } else {
                            Icons.Default.Lock
                        },
                        // TODO: use android resources for texts
                        contentDescription = "lock",
                        //    if(isPasswordVisible) {
                        //    stringResource(Res.string.hide_password)
                        //} else {
                        //    stringResource(Res.string.show_password)
                        //},
                        tint = MaterialTheme.colorScheme.extended.textDisabled,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = 24.dp
                                ),
                                onClick = onToggleVisibilityClick
                            )
                    )
                }
            }
        )

        if (supportingText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingText,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.extended.textTertiary
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
@Preview(name = "GuadSecureTextField Light", showBackground = true)
fun GuadSecureTextFieldLightPreview() {
    var isVisible by remember { mutableStateOf(false) }
    GuadTheme(
        darkTheme = false
    ) {
        GuadSecureTextField(
            state = rememberTextFieldState(),
            title = "Password",
            placeholder = "you strong password",
            supportingText = "at least one upper case letter, one lower case letter, one number and one special character",
            isPasswordVisible = isVisible,
            onToggleVisibilityClick = { isVisible = !isVisible }
        )
    }
}


@Composable
@Preview(name = "GuadSecureTextField Dark", showBackground = true)
fun GuadSecureTextFieldDarkPreview() {
    var isVisible by remember { mutableStateOf(false) }
    GuadTheme(
        darkTheme = false
    ) {
        GuadSecureTextField(
            state = rememberTextFieldState(),
            title = "Password",
            placeholder = "you strong password",
            supportingText = "at least one upper case letter, one lower case letter, one number and one special character",
            isPasswordVisible = isVisible,
            onToggleVisibilityClick = { isVisible = !isVisible }
        )
    }
}

@Composable
@Preview(name = "GuadSecureTextField Error", showBackground = true)
fun GuadSecureTextFieldErrorPreview() {
    var isVisible by remember { mutableStateOf(false) }
    GuadTheme(
        darkTheme = false
    ) {
        GuadSecureTextField(
            state = rememberTextFieldState(),
            isError = true,
            title = "Password",
            placeholder = "you strong password",
            supportingText = "at least one upper case letter, one lower case letter, one number and one special character",
            isPasswordVisible = isVisible,
            onToggleVisibilityClick = { isVisible = !isVisible }
        )
    }
}
