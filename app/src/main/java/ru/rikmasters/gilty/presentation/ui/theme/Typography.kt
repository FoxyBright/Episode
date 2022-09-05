@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val baseFontFamily = FontFamily(
    // Font(R.font., FontWeight.Medium)
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
    )
    // Добавить другие шрифты
)