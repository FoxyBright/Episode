package ru.rikmasters.gilty.shared.theme

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Gradients {
    @Composable
    fun green(): List<Color> {
        return listOf(
            colorScheme.surface,
            colorScheme.onSurface,
            colorScheme.surfaceTint
        )
    }

    @Composable
    fun red(): List<Color> {
        return listOf(
            colorScheme.surfaceVariant,
            colorScheme.inverseOnSurface,
        )
    }

    @Composable
    fun gray(): List<Color> {
        return listOf(
            colorScheme.onTertiary,
            colorScheme.onTertiary,
        )
    }

    @Composable
    fun blackTranslation(): List<Color> {
        return listOf(
            Color(0xFF070707),
            Color(0x20040404),
        )
    }

    @Composable
    fun blackTranslationReversed(): List<Color> {
        return listOf(
            Color(0xFF070707),
            Color(0x00040404),
            Color(0xFF070707)
        )
    }

}

