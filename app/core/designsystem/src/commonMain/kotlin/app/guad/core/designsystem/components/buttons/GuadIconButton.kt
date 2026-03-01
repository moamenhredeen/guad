package app.guad.core.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.guad.core.designsystem.theme.GuadTheme
import app.guad.core.designsystem.theme.extended

@Composable
fun GuadIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier
            .size(45.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary
        )
    ) {
        content()
    }
}


@Composable
@Preview(name = "Guad Icon Button Light")
fun GuadIconButtonPreview() {
    GuadTheme (darkTheme = false){
        GuadIconButton(
            onClick = {}
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
    }
}

@Composable
@Preview(name = "Guad Icon Button Dark")
fun GuadIconButtonPreviewDark() {
    GuadTheme (darkTheme = true){
        GuadIconButton(
            onClick = {}
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
    }
}
