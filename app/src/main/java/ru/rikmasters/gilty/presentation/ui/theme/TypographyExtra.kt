@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

data class ExtraTypography(
    val myExtraTypography: TextStyle = baseTextStyle,
    val button: TextStyle = baseTextStyle,
    val H3: TextStyle = baseTextStyle,
    val MediumText: TextStyle = baseTextStyle,
    val ExtraHeader: TextStyle = baseTextStyle,
    val CodeNumber: TextStyle = baseTextStyle,
    val buttonText: TextStyle = baseTextStyle
    // Добавить новый шрифт в схему = baseTextStyle
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "ThemeExtra.typography",
        "ru.ringmasters.gilty.presentation.ui.theme.base.ThemeExtra"
    )
)
val DefaultExtraTypography = ExtraTypography(

    myExtraTypography = baseTextStyle.copy(fontWeight = FontWeight.ExtraBold),

    button = baseTextStyle.copy(
        fontSize = 18.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold, color = Color.White
    ),

    H3 = baseTextStyle.copy(
        fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold
    ),

    MediumText = baseTextStyle.copy(
        fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Normal
    ),

    buttonText = baseTextStyle.copy(
        fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal
    ),

    ExtraHeader = baseTextStyle.copy(
        fontSize = 23.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold
    ),

    CodeNumber = baseTextStyle.copy(
        fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, color = Color.Black
    ),
    // Добавить в схему шрифты
)