package ru.rikmasters.gilty.shared.theme.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ColorScheme.switch() = copy(
    primary = animateDarkModeColor(primary),
    onPrimary = animateDarkModeColor(onPrimary),
    primaryContainer = animateDarkModeColor(primaryContainer),
    onPrimaryContainer = animateDarkModeColor(onPrimaryContainer),
    inversePrimary = animateDarkModeColor(inversePrimary),
    secondary = animateDarkModeColor(secondary),
    onSecondary = animateDarkModeColor(onSecondary),
    secondaryContainer = animateDarkModeColor(secondaryContainer),
    onSecondaryContainer = animateDarkModeColor(onSecondaryContainer),
    tertiary = animateDarkModeColor(tertiary),
    onTertiary = animateDarkModeColor(onTertiary),
    tertiaryContainer = animateDarkModeColor(tertiaryContainer),
    onTertiaryContainer = animateDarkModeColor(onTertiaryContainer),
    background = animateDarkModeColor(background),
    onBackground = animateDarkModeColor(onBackground),
    surface = animateDarkModeColor(surface),
    onSurface = animateDarkModeColor(onSurface),
    surfaceVariant = animateDarkModeColor(surfaceVariant),
    onSurfaceVariant = animateDarkModeColor(onSurfaceVariant),
    surfaceTint = animateDarkModeColor(surfaceTint),
    inverseSurface = animateDarkModeColor(inverseSurface),
    inverseOnSurface = animateDarkModeColor(inverseOnSurface),
    error = animateDarkModeColor(error),
    onError = animateDarkModeColor(onError),
    errorContainer = animateDarkModeColor(errorContainer),
    onErrorContainer = animateDarkModeColor(onErrorContainer),
    outline = animateDarkModeColor(outline),
    outlineVariant = animateDarkModeColor(outlineVariant),
    scrim = animateDarkModeColor(scrim),
)

@Composable
fun animateDarkModeColor(targetValue: Color) =
    animateColorAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 750)
    ).value