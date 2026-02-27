package app.guad.core.designsystem.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.theme.GuadTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class GuadButtonStyle {
    PRIMARY,
    DESTRUCTIVE_PRIMARY,

    SECONDARY,
    DESTRUCTIVE_SECONDARY,

    TEXT
}


@Composable
fun GuadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: GuadButtonStyle = GuadButtonStyle.PRIMARY,
    isLoading: Boolean = false,
    loadingIcon: @Composable (() -> Unit)? = null,
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        content = {
            if(isLoading) {
                loadingIcon?.invoke()
            } else {
                Text(text = text)
            }
        }
    )
}


@Preview(name = "Primary Button")
@Composable
fun GuadButtonPreview(){
    GuadTheme {
        GuadButton(
            text = "Button",
            onClick = {}
        )
    }
}

@Preview(name = "Secondary Button")
@Composable
fun DestructiveGuadButtonPreview(){
    GuadTheme {
        GuadButton(
            text = "Button",
            onClick = {},
            style = GuadButtonStyle.DESTRUCTIVE_PRIMARY
        )
    }
}
