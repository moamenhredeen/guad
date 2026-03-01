package app.guad.core.designsystem.components.brand

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.guad.core.designsystem.theme.GuadTheme
import guad.core.designsystem.generated.resources.Res
import guad.core.designsystem.generated.resources.logo
import org.jetbrains.compose.resources.vectorResource


@Composable
fun GuadLogo(modifier: Modifier = Modifier) {
    Icon(
        imageVector = vectorResource(Res.drawable.logo),
        contentDescription = "Logo",
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
@Preview(name = "GuadLogo Light")
fun GuadLogoLightPreview() {
    GuadTheme(
        darkTheme = false
    ) {
        GuadLogo()
    }
}


@Composable
@Preview(name = "GuadLogo Dark")
fun GuadLogoDarkPreview() {
    GuadTheme(
        darkTheme = true
    ) {
        GuadLogo()
    }
}
