@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val myExtraColor: Color = Color.Unspecified
    // Добавить новый цвет в схему = Color.Unspecified
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.colors", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val LightExtraColors = ExtraColors(
    myExtraColor = Colors.Purple40
    // Добавить в схему цвета из файла Colors
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.colors", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val DarkExtraColors = ExtraColors(
    myExtraColor = Colors.Purple80
    // Добавить в схему цвета из файла Colors
)
