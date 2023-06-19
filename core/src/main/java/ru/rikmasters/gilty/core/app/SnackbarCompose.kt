package ru.rikmasters.gilty.core.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp

@Composable
fun GSnackbar(
    data: SnackbarData,
) {
    Snackbar(
        snackbarData = data,
        modifier = Modifier
            .padding(bottom = 24.dp),
        actionOnNewLine = false,
        shape = shapes.large,
        containerColor = colorScheme.inverseSurface,
        contentColor = colorScheme.inverseOnSurface,
        actionColor = colorScheme.primary,
        actionContentColor = colorScheme.secondary,
        dismissActionContentColor = colorScheme.outline
    )
}

/**
 * [ACTION & DISMISS COLOR ARE TRANSPARENT]
 */
@Composable
fun HalfTransparentSnackbar(
    data: SnackbarData,
) {
    Snackbar(
        snackbarData = data,
        modifier = Modifier
            .padding(bottom = 17.dp),
        actionOnNewLine = false,
        shape = shapes.large,
        containerColor = Color(0x305E5E5E),
        contentColor = White,
        actionColor = Transparent,
        actionContentColor = Transparent,
        dismissActionContentColor = Transparent
    )
}