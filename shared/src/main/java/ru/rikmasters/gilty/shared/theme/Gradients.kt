package ru.rikmasters.gilty.shared.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Gradients {
    @Composable
    fun green(): List<Color> {
        return listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.surfaceTint
        )
    }

    @Composable
    fun red(): List<Color> {
        return listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.inverseOnSurface,
        )
    }
}

