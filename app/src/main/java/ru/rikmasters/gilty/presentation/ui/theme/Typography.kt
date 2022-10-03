@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

private val baseFontFamily = FontFamily(
    Font(R.font.gilroy_regular),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_bold, FontWeight.Bold)
)

@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.typography", "androidx.compose.material3.MaterialTheme"))
val baseTextStyle = TextStyle(
    fontFamily = baseFontFamily
    // Настроить
)

@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.typography", "androidx.compose.material3.MaterialTheme"))
val Typography = Typography(
    bodyLarge = baseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
        // Настроить
    ),
    titleLarge = baseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),

    labelSmall = baseTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 17.sp
    ),
    bodyMedium = baseTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    // Добавить другие шрифты
)