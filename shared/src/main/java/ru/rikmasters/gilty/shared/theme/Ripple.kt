package ru.rikmasters.gilty.shared.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Ripple : RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Gray


    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.White, isSystemInDarkTheme()
    )
}