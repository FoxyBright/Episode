package ru.rikmasters.gilty.core.app

import androidx.compose.runtime.Composable

interface AppTheme {
    @Composable
    fun apply(
        darkMode: Boolean,
        dynamicColor: Boolean,
        content: @Composable () -> Unit
    )
}