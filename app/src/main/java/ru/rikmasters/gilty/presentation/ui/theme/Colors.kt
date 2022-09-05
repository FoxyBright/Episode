@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Pink40
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Pink80
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Purple40
import ru.rikmasters.gilty.presentation.ui.theme.Colors.Purple80
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
    // Добавить новые цвета
}


@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme", "androidx.compose.material3.MaterialTheme"))
val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    // Добавить в схему
)

@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme", "androidx.compose.material3.MaterialTheme"))
val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
    // Добавить в схему
)