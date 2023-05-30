@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.toSp

private val baseFontFamily = FontFamily(
    Font(R.font.gilroy_regular, Normal),
    Font(R.font.gilroy_medium, Medium),
    Font(R.font.gilroy_semibold, SemiBold),
    Font(R.font.gilroy_bold, Bold)
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "MaterialTheme.typography",
        "androidx.compose.material3.MaterialTheme"
    )
)
val baseTextStyle = TextStyle(
    fontFamily = baseFontFamily
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "MaterialTheme.typography",
        "androidx.compose.material3.MaterialTheme"
    )
)
val Typography = @Composable {
    Typography(  //FONTS ШРИФТ - это для удобства поиска
        /*      SMALL TYPOGRAPHY      */
        bodySmall = baseTextStyle.copy(),
        displaySmall = baseTextStyle.copy(
            fontSize = 8.dp.toSp(),
            fontWeight = Medium,
            lineHeight = 10.dp.toSp()
        ),
        titleSmall = baseTextStyle.copy(
            fontSize = 10.dp.toSp(),
            fontWeight = Medium,
            lineHeight = 12.dp.toSp()
        ),
        headlineSmall = baseTextStyle.copy(
            fontSize = 12.dp.toSp(),
            fontWeight = Medium,
            lineHeight = 16.dp.toSp()
        ),
        labelSmall = baseTextStyle.copy(
            fontSize = 14.dp.toSp(),
            fontWeight = Medium,
            lineHeight = 18.dp.toSp()
        ),
        
        /*      MEDIUM TYPOGRAPHY      */
        bodyMedium = baseTextStyle.copy(
            fontSize = 16.dp.toSp(),
            fontWeight = Medium,
            lineHeight = 20.dp.toSp()
        ),
        headlineMedium = baseTextStyle.copy(),
        labelMedium = baseTextStyle.copy(),
        titleMedium = baseTextStyle.copy(),
        displayMedium = baseTextStyle.copy(),
        
        /*      LARGE TYPOGRAPHY      */
        bodyLarge = baseTextStyle.copy(
            fontSize = 18.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 22.dp.toSp()
        ),
        labelLarge = baseTextStyle.copy(
            fontSize = 20.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 20.dp.toSp()
        ),
        headlineLarge = baseTextStyle.copy(
            fontSize = 22.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 22.dp.toSp()
        ),
        displayLarge = baseTextStyle.copy(
            fontSize = 24.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 32.dp.toSp()
        ),
        titleLarge = baseTextStyle.copy(
            fontSize = 28.dp.toSp(),
            fontWeight = Bold,
            lineHeight = 32.dp.toSp()
        )
    )
}