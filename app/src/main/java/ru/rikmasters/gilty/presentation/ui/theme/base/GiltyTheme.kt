@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme.base

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import ru.rikmasters.gilty.core.app.AppTheme
import ru.rikmasters.gilty.presentation.ui.theme.DarkColorScheme
import ru.rikmasters.gilty.presentation.ui.theme.DarkExtraColors
import ru.rikmasters.gilty.presentation.ui.theme.DefaultExtraShapes
import ru.rikmasters.gilty.presentation.ui.theme.DefaultExtraTypography
import ru.rikmasters.gilty.presentation.ui.theme.ExtraColors
import ru.rikmasters.gilty.presentation.ui.theme.ExtraShapes
import ru.rikmasters.gilty.presentation.ui.theme.ExtraTypography
import ru.rikmasters.gilty.presentation.ui.theme.LightColorScheme
import ru.rikmasters.gilty.presentation.ui.theme.LightExtraColors
import ru.rikmasters.gilty.presentation.ui.theme.Shapes
import ru.rikmasters.gilty.presentation.ui.theme.Typography

object GiltyTheme: AppTheme {
    @Composable
    override fun apply(
        darkMode: Boolean,
        dynamicColor: Boolean,
        content: @Composable () -> Unit
    ) = GiltyTheme(darkMode, dynamicColor, content)
}

@Composable
fun GiltyTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val (colorScheme, extraColors) = resolveColors(
        LocalContext.current, darkMode, dynamicColor,
    )

    CompositionLocalProvider(
        LocalExtraColors provides extraColors,
        LocalExtraTypography provides DefaultExtraTypography,
        LocalExtraShapes provides DefaultExtraShapes,
    ) {

        MaterialTheme(
            colorScheme = colorScheme.switch(),
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