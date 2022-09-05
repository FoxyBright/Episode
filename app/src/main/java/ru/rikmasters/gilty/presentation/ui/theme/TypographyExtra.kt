@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

data class ExtraTypography(
    val myExtraTypography: TextStyle = baseTextStyle
    // Добавить новый шрифт в схему = baseTextStyle
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.typography", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val DefaultExtraTypography = ExtraTypography(
    myExtraTypography = baseTextStyle.copy(fontWeight = FontWeight.ExtraBold)
    // Добавить в схему шрифты
)