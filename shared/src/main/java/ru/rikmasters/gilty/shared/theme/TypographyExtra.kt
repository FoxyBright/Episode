
@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.*
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.*
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.common.extentions.toSp
import ru.rikmasters.gilty.shared.theme.Colors.FireRed
import ru.rikmasters.gilty.shared.theme.Colors.OrangeRed

data class ExtraTypography(
    val CodeText: TextStyle = baseTextStyle,
    val RatingText: TextStyle = baseTextStyle,
    val RatingSmallText: TextStyle = baseTextStyle,
    val TranslationTitlePreview: TextStyle = baseTextStyle,
    val TranslationSmallButton: TextStyle = baseTextStyle,
    val TranslationAuthorName: TextStyle = baseTextStyle,
    val TranslationBadge: TextStyle = baseTextStyle
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
val DefaultExtraTypography = @Composable {
    ExtraTypography(
        RatingText = baseTextStyle.copy(
            brush = GrBrush,
            fontSize = 46.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 56.dp.toSp()
        ),
        
        RatingSmallText = baseTextStyle.copy(
            brush = GrBrush,
            fontSize = 38.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 45.dp.toSp()
        ),
        
        CodeText = baseTextStyle.copy(
            fontSize = 22.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 38.dp.toSp(),
            textAlign = TextAlign.Center
        ),
        
        TranslationTitlePreview = baseTextStyle.copy(
            fontSize = 20.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 23.dp.toSp()
        ),
        
        TranslationSmallButton = baseTextStyle.copy(
            fontSize = 14.dp.toSp(),
            fontWeight = SemiBold,
            lineHeight = 17.dp.toSp()
        ),
        
        TranslationBadge = baseTextStyle.copy(
            fontSize = 11.dp.toSp(),
            fontWeight = Normal,
            lineHeight = 16.dp.toSp()
        )
        
        // Add fonts in scheme
    )
}