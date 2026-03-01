package app.guad.core.designsystem.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.components.brand.GuadLogo
import app.guad.core.designsystem.theme.GuadTheme
import app.guad.core.presentation.util.DeviceConfiguration
import app.guad.core.presentation.util.currentDeviceConfiguration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GuadAdaptiveResultLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = currentDeviceConfiguration()

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        if(configuration == DeviceConfiguration.MOBILE_PORTRAIT) {

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
                    GuadLogo()
                    Spacer(modifier = Modifier.height(32.dp))
                    Surface (
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            content()
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                if(configuration != DeviceConfiguration.MOBILE_LANDSCAPE) {
                    GuadLogo()
                }
                Column(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
@Preview
fun GuadAdaptiveResultLayoutPreview() {
    GuadTheme {
        GuadAdaptiveResultLayout(
            modifier = Modifier
                .fillMaxSize(),
            content = {
                Text(
                    text = "Registration successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}