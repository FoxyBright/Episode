@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import ru.rikmasters.gilty.presentation.ui.theme.*

@Composable
fun GiltyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val (colorScheme, extraColors) = resolveColors(
        LocalContext.current, darkTheme, dynamicColor,
    )

    //paintStatusBar(colorScheme, darkTheme)

    CompositionLocalProvider(
        LocalExtraColors provides extraColors,
        LocalExtraTypography provides DefaultExtraTypography,
        LocalExtraShapes provides DefaultExtraShapes,
    ) {

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

private fun resolveColors(
    context: Context,
    darkTheme: Boolean,
    dynamicColor: Boolean
): Pair<ColorScheme, ExtraColors> = when {

    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if(darkTheme) dynamicDarkColorScheme(context) to DarkExtraColors
        else dynamicLightColorScheme(context) to LightExtraColors
    }
    darkTheme -> DarkColorScheme to DarkExtraColors
    else -> LightColorScheme to LightExtraColors
}

@SuppressLint("ComposableNaming")
@Composable
private fun paintStatusBar(colorScheme: ColorScheme, darkTheme: Boolean) {
    val view = LocalView.current
    if(!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }
}

object ThemeExtra {

    val colors: ExtraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtraColors.current

    val typography: ExtraTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalExtraTypography.current

    val shapes: ExtraShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalExtraShapes.current
}

private val LocalExtraColors = staticCompositionLocalOf { ExtraColors() }

private val LocalExtraTypography = staticCompositionLocalOf { ExtraTypography() }

private val LocalExtraShapes = staticCompositionLocalOf { ExtraShapes() }