package app.guad.core.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.theme.GuadTheme
import app.guad.core.designsystem.theme.extended

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
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {

    val colors = when(style) {
        GuadButtonStyle.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        GuadButtonStyle.DESTRUCTIVE_PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        GuadButtonStyle.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        GuadButtonStyle.DESTRUCTIVE_SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.error,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        GuadButtonStyle.TEXT -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
    }


    val defaultBorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.extended.disabledOutline
    )
    val border = when {
        style == GuadButtonStyle.PRIMARY && !enabled -> defaultBorderStroke
        style == GuadButtonStyle.SECONDARY -> defaultBorderStroke
        style == GuadButtonStyle.DESTRUCTIVE_PRIMARY && !enabled -> defaultBorderStroke
        style == GuadButtonStyle.DESTRUCTIVE_SECONDARY -> {
            val borderColor = if(enabled) {
                MaterialTheme.colorScheme.extended.destructiveSecondaryOutline
            } else {
                MaterialTheme.colorScheme.extended.disabledOutline
            }
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        }
        else -> null
    }

    Button(
        colors = colors,
        border = border,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        modifier = modifier,
    ) {

        Box(
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = Color.Black,
                strokeWidth = 1.5.dp,
                modifier = Modifier
                    .size(16.dp)
                    .alpha(if (isLoading) 1f else 0f),
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(if (isLoading) 0f else 1f),
            ){
                leadingIcon?.invoke()
                Text(text = text)
            }
        }
    }
}


@Preview(name = "Guad Button")
@Composable
fun GuadButtonPreview(){

    var isLoading by remember { mutableStateOf(false) }

    val modifier = Modifier
        .width(200.dp)

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GuadTheme {
            GuadButton(
                modifier = modifier,
                text = "Primary",
                style = GuadButtonStyle.PRIMARY,
                onClick = {
                    isLoading = !isLoading
                },
                isLoading = isLoading,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )


            GuadButton(
                modifier = modifier,
                text = "Primary Destructive",
                style = GuadButtonStyle.DESTRUCTIVE_PRIMARY,
                onClick = {
                    isLoading = !isLoading
                },
                isLoading = isLoading,
            )


            GuadButton(
                modifier = modifier,
                text = "Secondary",
                style = GuadButtonStyle.SECONDARY,
                onClick = {
                    isLoading = !isLoading
                },
                isLoading = isLoading,
            )

            GuadButton(
                modifier = modifier,
                text = "Secondary Destructive",
                style = GuadButtonStyle.DESTRUCTIVE_SECONDARY,
                onClick = {
                    isLoading = !isLoading
                },
                isLoading = isLoading,
            )


            GuadButton(
                modifier = modifier,
                text = "Text",
                style = GuadButtonStyle.TEXT,
                onClick = {
                    isLoading = !isLoading
                },
                isLoading = isLoading,
            )
        }
    }
}
