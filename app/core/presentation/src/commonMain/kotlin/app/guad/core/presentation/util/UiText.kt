package app.guad.core.presentation.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * UiText
 */
sealed interface UiText {

    /**
     * DynamicString
     */
    data class DynamicString(val value: String) : UiText

    /**
     * StaticString
     */
    class StaticString(
        val id: StringResource,
        val args: Array<Any> = arrayOf()
    ) : UiText

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StaticString -> stringResource(
                resource = id,
                formatArgs = args
            )
        }
    }

    suspend fun asStringAsync(): String {
        return when (this) {
            is DynamicString -> value
            is StaticString -> getString(
                resource = id,
                formatArgs = args
            )
        }
    }
}