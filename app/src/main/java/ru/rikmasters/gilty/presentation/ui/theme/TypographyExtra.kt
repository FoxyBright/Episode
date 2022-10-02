@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class ExtraTypography(
    val myExtraTypography: TextStyle = baseTextStyle,
    val button: TextStyle = baseTextStyle,
    val H3: TextStyle = baseTextStyle
    // Добавить новый шрифт в схему = baseTextStyle
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.typography", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val DefaultExtraTypography = ExtraTypography(

    myExtraTypography = baseTextStyle.copy(fontWeight = FontWeight.ExtraBold),

    button = baseTextStyle.copy(
        fontSize = 18.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold, color = Color.White
    ),

    H3 = baseTextStyle.copy(
        fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold
    )
    // Добавить в схему шрифты
)