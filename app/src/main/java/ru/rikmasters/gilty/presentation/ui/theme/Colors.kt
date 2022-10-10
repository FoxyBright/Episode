@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.presentation.ui.theme.Colors.BackgroundDay
import ru.rikmasters.gilty.presentation.ui.theme.Colors.BackgroundNight
import ru.rikmasters.gilty.presentation.ui.theme.Colors.NotActiveDay
import ru.rikmasters.gilty.presentation.ui.theme.Colors.NotActiveNight
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Pink40
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Pink80
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Primary
import ru.rikmasters.gilty.presentation.ui.theme.Colors.PurpleGrey40
import ru.rikmasters.gilty.presentation.ui.theme.Colors.PurpleGrey80

@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme", "androidx.compose.material3.MaterialTheme"))
object Colors {

    val Purple80 = Color(0xFFD0BCFF)
    val PurpleGrey80 = Color(0xFFCCC2DC)
    val Pink80 = Color(0xFFEFB8C8)

    val Purple40 = Color(0xFF6650a4)
    val PurpleGrey40 = Color(0xFF625b71)
    val Pink40 = Color(0xFF7D5260)

    val Primary = Color(0xFFFF4745)
    val Green = Color(0xFF35C65A)
    val NotActiveDay = Color(0xFFFD7C7A)
    val Gray2 = Color(0xFFC9C5CA)
    val Gradient1 = Color(0xFFFF6645)
    val Gradient2 = Color(0xFFDF2B4B)
    val Gray = Color(0xFFE9E9EA)
    val Border = Color(0xFFB0B0B0)
    val Divider = Color(0xFFDFDFDF)

    val BackgroundDay = Color(0xFFF6F6F6)
    val CardBackgroundDay = Color.White
    val PrimaryTextDay = Color(0xFF1C1B1F)
    val SecondaryTextDay = Color(0xFFAEAAAE)
    val searchCardBackgroundDay = Color(0xFFF6F6F6)

    //Night mod colors
    val BackgroundNight = Color(0xFF1F1F1F)
    val NotActiveNight = Color(0x1FE3E3E3)
    val CardBackgroundNight = Color(0xFF353535)
    val PrimaryTextNight = Color.White
    val SecondaryTextNight = Color(0xFFE6E1E5)
    val searchCardBackgroundNight = Color(0x1FE3E3E3)

    // Добавить новые цвета
}


@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme", "androidx.compose.material3.MaterialTheme"))
val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = NotActiveDay,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundDay
    // Добавить в схему
)

@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme", "androidx.compose.material3.MaterialTheme"))
val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = NotActiveNight,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = BackgroundNight
    // Добавить в схему
)