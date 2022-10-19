@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

data class ExtraTypography(
    val myExtraTypography: TextStyle = baseTextStyle,
    val button: TextStyle = baseTextStyle,
    val MediumText: TextStyle = baseTextStyle,
    val ExtraHeader: TextStyle = baseTextStyle,
    val buttonText: TextStyle = baseTextStyle,
    val H1: TextStyle = baseTextStyle,
    val H2: TextStyle = baseTextStyle,
    val H3: TextStyle = baseTextStyle,
    val Body1Bold: TextStyle = baseTextStyle,
    val Body1Medium: TextStyle = baseTextStyle,
    val Body1Sb: TextStyle = baseTextStyle,
    val SubHeadSb: TextStyle = baseTextStyle,
    val SubHeadMedium: TextStyle = baseTextStyle,
    val SubHeadEb: TextStyle = baseTextStyle,
    val SubHeadBold: TextStyle = baseTextStyle,
    val ButtonLabelText: TextStyle = baseTextStyle,
    val Body2Bold: TextStyle = baseTextStyle,
    val LabelText: TextStyle = baseTextStyle,
    val ProfileLabelText: TextStyle = baseTextStyle,
    val ProfileObserversText: TextStyle = baseTextStyle,
    val CodeText: TextStyle = baseTextStyle,
    val RatingText: TextStyle = baseTextStyle
    // Добавить новый шрифт в схему = baseTextStyle
)

@OptIn(ExperimentalTextApi::class)
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

    MediumText = baseTextStyle.copy(
        fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Normal
    ),

    buttonText = baseTextStyle.copy(
        fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal,
    ),

    ExtraHeader = baseTextStyle.copy(
        fontSize = 23.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold
    ),

    H1 = baseTextStyle.copy(
        fontSize = 28.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold
    ),

    H2 = baseTextStyle.copy(
        fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold
    ),

    H3 = baseTextStyle.copy(
        fontSize = 20.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold
    ),

    Body2Bold = baseTextStyle.copy(
        fontSize = 18.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold
    ),

    Body1Bold = baseTextStyle.copy(
        fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold
    ),

    Body1Sb = baseTextStyle.copy(
        fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold
    ),

    Body1Medium = baseTextStyle.copy(
        fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium
    ),

    SubHeadSb = baseTextStyle.copy(
        fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold
    ),

    SubHeadEb = baseTextStyle.copy(
        fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.ExtraBold
    ),

    SubHeadMedium = baseTextStyle.copy(
        fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium
    ),

    SubHeadBold = baseTextStyle.copy(
        fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold
    ),

    LabelText = baseTextStyle.copy(
        fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium
    ),

    ButtonLabelText = baseTextStyle.copy(
        fontSize = 13.sp, lineHeight = 15.sp, fontWeight = FontWeight.Medium, color = Color.White
    ),

    ProfileLabelText = baseTextStyle.copy(
        fontSize = 12.sp, lineHeight = 13.sp, fontWeight = FontWeight.SemiBold
    ),

    ProfileObserversText = baseTextStyle.copy(
        fontSize = 9.sp, lineHeight = 9.sp, fontWeight = FontWeight.Medium
    ),

    RatingText = baseTextStyle.copy(
        fontSize = 38.sp,
        lineHeight = 46.sp,
        fontWeight = FontWeight.Bold,
        brush = Brush.horizontalGradient(0f to Colors.Gradient1, 1000f to Colors.Gradient2)
    ),

    CodeText = baseTextStyle.copy(
        fontSize = 22.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),


// Добавить в схему шрифты
)