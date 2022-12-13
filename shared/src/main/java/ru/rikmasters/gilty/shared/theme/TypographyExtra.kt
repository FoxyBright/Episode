@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.*
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.*
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.theme.Colors.FireRed
import ru.rikmasters.gilty.shared.theme.Colors.OrangeRed

data class ExtraTypography(
    val CodeText: TextStyle = baseTextStyle,
    val RatingText: TextStyle = baseTextStyle,
    // Add new fonts in scheme = baseTextStyle
)

private val GrBrush =
    Brush.horizontalGradient(
        0f to OrangeRed, 1000f to FireRed
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
    RatingText = baseTextStyle.copy(
        brush = GrBrush,
        fontSize = 46.sp,
        fontWeight = Bold,
        lineHeight = 56.sp
    ),
    CodeText = baseTextStyle.copy(
        fontSize = 22.sp,
        fontWeight = Bold,
        lineHeight = 38.sp,
        textAlign = TextAlign.Center
    ),
    // Add fonts in scheme
)