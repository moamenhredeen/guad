package app.guad.previews

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import app.guad.core.designsystem.components.brand.GuadLogo
import app.guad.core.designsystem.layouts.GuadAdaptiveFormLayout
import app.guad.core.designsystem.theme.GuadTheme

@Preview
@PreviewLightDark
@PreviewScreenSizes
@Composable
fun GuadAdaptiveFormLayoutPreview() {
    GuadTheme {
        GuadAdaptiveFormLayout(
            headerText = "Welcome to Guad!",
            errorText = "Login failed!",
            logo = { GuadLogo() },
        ) {
            Text(
                text = "Sample form title",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Sample form title 2",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
