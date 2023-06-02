@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import ru.rikmasters.gilty.core.app.AppTheme
import ru.rikmasters.gilty.shared.theme.*
import ru.rikmasters.gilty.shared.theme.Typography

object GiltyTheme: AppTheme {
    
    @SuppressLint("ComposableNaming")
    @Composable
    override fun apply(
        darkMode: Boolean,
        dynamicColor: Boolean,
        content: @Composable () -> Unit,
    ) = GiltyTheme(darkMode, dynamicColor, content)
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun GiltyTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val (colorScheme, extraColors) = resolveColors(
        context = LocalContext.current,
        darkTheme = darkMode,
        dynamicColor = dynamicColor,
    )
    CompositionLocalProvider(
        LocalExtraColors provides extraColors,
        LocalExtraTypography provides DefaultExtraTypography(),
        LocalExtraShapes provides DefaultExtraShapes,
    ) {
        MaterialTheme(
            colorScheme = colorScheme.switch(),
            shapes = Shapes,
            typography = Typography(),
        ) {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null,
                LocalRippleTheme provides Ripple,
                content = content
            )
        }
    }
    val view = LocalView.current
    if(!view.isInEditMode) {
        SideEffect {
            (view.context as Activity)
                .window.statusBarColor = colorScheme.background.toArgb()
            ViewCompat.getWindowInsetsController(view)?.let {
                it.isAppearanceLightStatusBars = !darkMode
            }
        }
    }
}

private fun resolveColors(
    context: Context,
    darkTheme: Boolean,
    dynamicColor: Boolean,
): Pair<ColorScheme, ExtraColors> = when {
    
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if(darkTheme) dynamicDarkColorScheme(context) to DarkExtraColors
        else dynamicLightColorScheme(context) to LightExtraColors
    }
    
    darkTheme -> DarkColorScheme to DarkExtraColors
    else -> LightColorScheme to LightExtraColors
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

private val LocalExtraTypography =
    staticCompositionLocalOf { ExtraTypography() }

private val LocalExtraShapes = staticCompositionLocalOf { ExtraShapes() }