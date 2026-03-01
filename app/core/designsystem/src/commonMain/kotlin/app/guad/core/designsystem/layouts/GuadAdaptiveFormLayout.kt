package app.guad.core.designsystem.layouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.components.brand.GuadLogo
import app.guad.core.designsystem.theme.GuadTheme
import app.guad.core.designsystem.theme.extended
import app.guad.core.presentation.util.DeviceConfiguration
import app.guad.core.presentation.util.currentDeviceConfiguration

@Composable
fun GuadAdaptiveFormLayout(
    headerText: String,
    errorText: String? = null,
    logo: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = currentDeviceConfiguration()
    val headerColor = if (configuration == DeviceConfiguration.MOBILE_LANDSCAPE) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.extended.textPrimary
    }

    when (configuration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            Surface (
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(WindowInsets.navigationBars)
                        .consumeWindowInsets(WindowInsets.displayCutout),
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    logo()
                    Spacer(modifier = Modifier.height(32.dp))
                    Surface (
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))
                            AuthHeaderSection(
                                headerText = headerText,
                                headerColor = headerColor,
                                errorText = errorText
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            content()
                        }
                    }
                }
            }
        }

        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Surface (
                color = MaterialTheme.colorScheme.background,
            ){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = modifier
                        .consumeWindowInsets(WindowInsets.displayCutout)
                        .consumeWindowInsets(WindowInsets.navigationBars)
                        .fillMaxSize(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        logo()
                        AuthHeaderSection(
                            headerText = headerText,
                            headerColor = headerColor,
                            errorText = errorText,
                            textAlign = TextAlign.Start
                        )
                    }
                    Surface (
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            content()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                logo()
                Column(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthHeaderSection(
                        headerText = headerText,
                        headerColor = headerColor,
                        errorText = errorText
                    )
                    content()
                }
            }
        }
    }
}

@Composable
fun ColumnScope.AuthHeaderSection(
    headerText: String,
    headerColor: Color,
    errorText: String? = null,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = headerText,
        style = MaterialTheme.typography.titleLarge,
        color = headerColor,
        textAlign = textAlign,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
    AnimatedVisibility(
        visible = errorText != null
    ) {
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = textAlign
            )
        }
    }
}

@Composable
@Preview
fun GuadAdaptiveFormLayoutLightPreview() {
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

@Composable
@Preview
fun GuadAdaptiveFormLayoutDarkPreview() {
    GuadTheme(darkTheme = true) {
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