package app.guad.core.designsystem.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.theme.GuadTheme
import app.guad.core.designsystem.theme.extended
import androidx.compose.ui.tooling.preview.Preview
import app.guad.core.designsystem.components.buttons.GuadButton
import app.guad.core.designsystem.components.buttons.GuadButtonStyle
import guad.core.designsystem.generated.resources.Res
import guad.core.designsystem.generated.resources.success_checkmark
import org.jetbrains.compose.resources.vectorResource

@Composable
fun GuadSimpleResultLayout(
    title: String,
    description: String,
    icon: @Composable ColumnScope.() -> Unit,
    primaryButton: @Composable () -> Unit,
    secondaryButton: @Composable (() -> Unit)? = null,
    secondaryError: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -(25).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            primaryButton()

            if(secondaryButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                secondaryButton()
                if(secondaryError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = secondaryError,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@Preview
fun GuadSimpleSuccessLayoutPreview() {
    GuadTheme(darkTheme = true) {
        GuadSimpleResultLayout(
            title = "Hello world!",
            description = "Test description",
            icon = {
                Icon(
                    imageVector = vectorResource(Res.drawable.success_checkmark),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.extended.success,
                )
            },
            primaryButton = {
                GuadButton(
                    text = "Log In",
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            secondaryButton = {
                GuadButton(
                    text = "Resend verification email",
                    onClick = {},
                    style = GuadButtonStyle.SECONDARY,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        )
    }
}