package ru.rikmasters.gilty.shared.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material.ripple.RippleTheme.Companion.defaultRippleAlpha
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White

object Ripple: RippleTheme {
    
    @Composable
    override fun defaultColor() = Color.Gray
    
    @Composable
    override fun rippleAlpha() = defaultRippleAlpha(
        White, isSystemInDarkTheme()
    )
}