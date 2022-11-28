@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.R

private val baseFontFamily = FontFamily(
    Font(R.font.gilroy_regular),
    Font(R.font.gilroy_medium, Medium),
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
val Typography = Typography(
    /*      SMALL TYPOGRAPHY      */
    headlineSmall = baseTextStyle.copy(fontSize = 12.sp, fontWeight = Medium, lineHeight = 16.sp),
    titleSmall = baseTextStyle.copy(fontSize = 10.sp, fontWeight = Medium, lineHeight = 12.sp),
    labelSmall = baseTextStyle.copy(fontSize = 14.sp, fontWeight = Medium, lineHeight = 18.sp),
    displaySmall = baseTextStyle.copy(fontSize = 8.sp, fontWeight = Medium, lineHeight = 10.sp),
    bodySmall = baseTextStyle.copy(),

    /*      MEDIUM TYPOGRAPHY      */
    bodyMedium = baseTextStyle.copy(fontSize = 16.sp, fontWeight = Medium, lineHeight = 20.sp),
    headlineMedium = baseTextStyle.copy(),
    labelMedium = baseTextStyle.copy(),
    titleMedium = baseTextStyle.copy(),
    displayMedium = baseTextStyle.copy(),

    /*      LARGE TYPOGRAPHY      */
    bodyLarge = baseTextStyle.copy(fontSize = 18.sp, fontWeight =  Bold, lineHeight = 22.sp),
    labelLarge = baseTextStyle.copy(fontSize = 20.sp, fontWeight = Bold, lineHeight = 20.sp),
    displayLarge = baseTextStyle.copy(fontSize = 24.sp, fontWeight =  Bold, lineHeight = 32.sp),
    titleLarge = baseTextStyle.copy(fontSize = 28.sp, fontWeight = Bold, lineHeight = 32.sp),
    headlineLarge = baseTextStyle.copy(fontSize = 22.sp, fontWeight = Bold, lineHeight = 22.sp)
)