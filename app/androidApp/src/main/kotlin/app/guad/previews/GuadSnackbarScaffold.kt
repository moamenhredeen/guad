package app.guad.previews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.components.buttons.GuadButton
import app.guad.core.designsystem.layouts.GuadSnackbarScaffold
import kotlinx.coroutines.launch

@Preview
@Composable
private fun GuadSnackbarScaffoldPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    GuadSnackbarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text("hello world")
            Spacer(modifier = Modifier.height(24.dp))
            GuadButton(
                text = "Click me",
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Hello world")
                    }
                }
            )
        }
    }
}