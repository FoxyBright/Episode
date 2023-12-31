package ru.rikmasters.gilty.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun GSnackbar(
    data: SnackbarData
) {
    Snackbar(
        data,
        Modifier.padding(bottom = 24.dp),
        false,
        MaterialTheme.shapes.large,
        MaterialTheme.colorScheme.inverseSurface,
        MaterialTheme.colorScheme.inverseOnSurface,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.outline
    )
}

/**
 * [ACTION & DISMISS COLOR ARE TRANSPARENT]
 */
@Composable
fun HalfTransparentSnackbar(
    data: SnackbarData
) {
    Snackbar(
        data,
        Modifier.padding(bottom = 17.dp),
        false,
        MaterialTheme.shapes.large,
        ThemeExtra.colors.thirdOpaqueGray,
        ThemeExtra.colors.white,
        Color.Transparent,
        Color.Transparent,
        Color.Transparent
    )
}