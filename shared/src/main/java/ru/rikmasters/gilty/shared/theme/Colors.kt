@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.shared.theme.Colors.AlmostDark
import ru.rikmasters.gilty.shared.theme.Colors.AlmostRed
import ru.rikmasters.gilty.shared.theme.Colors.Anthracite
import ru.rikmasters.gilty.shared.theme.Colors.Ash
import ru.rikmasters.gilty.shared.theme.Colors.Black
import ru.rikmasters.gilty.shared.theme.Colors.DimGrin
import ru.rikmasters.gilty.shared.theme.Colors.DimRed
import ru.rikmasters.gilty.shared.theme.Colors.FireRed
import ru.rikmasters.gilty.shared.theme.Colors.Green
import ru.rikmasters.gilty.shared.theme.Colors.Lead
import ru.rikmasters.gilty.shared.theme.Colors.LightGray
import ru.rikmasters.gilty.shared.theme.Colors.LightGrin
import ru.rikmasters.gilty.shared.theme.Colors.OrangeRed
import ru.rikmasters.gilty.shared.theme.Colors.PreDark
import ru.rikmasters.gilty.shared.theme.Colors.SaladGrin
import ru.rikmasters.gilty.shared.theme.Colors.Silver
import ru.rikmasters.gilty.shared.theme.Colors.SuperDark
import ru.rikmasters.gilty.shared.theme.Colors.White
import ru.rikmasters.gilty.shared.theme.Colors.YellowGrin
import ru.rikmasters.gilty.shared.theme.Colors.Zircon


@Deprecated(
    "Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme",
        "androidx.compose.material3.MaterialTheme")
)

@Suppress("unused")
object Colors {
    
    /*     UNIVERSAL COLORS    */
    val White = Color(0xFFFFFFFF) // ContainerDay && TextNight
    val Black = Color(0xFF000000) // Just Black
    val AlmostRed = Color(0xFFFF2E00) // PrimaryRed
    val Green = Color(0xFF09D93E) // PrimaryGreen
    val YellowGrin = Color(0xFFB9F905) // GreenGradient1
    val SaladGrin = Color(0xFF91F132) // GreenGradient2
    val LightGrin = Color(0xFF2DE45C) // GreenGradient3
    val DimGrin = Color(0xFF66EE89) // GreenInactive
    val OrangeRed = Color(0xFFFF5E2B) // RedGradient1
    val FireRed = Color(0xFFFF002D) // RedGradient2
    val DimRed = Color(0xFFFF6462) // RedInactive
    val WhiteOnSurface = Color(0xFFE6E1E5) // Tint for Back Button

    /*     DAY COLORS    */
    val LightGray = Color(0xFFF6F6F6) // BackgroundDay
    val PreDark = Color(0xFF1C1B1F) // TextDay
    val Silver = Color(0xFFAEAAAE) // TextSecondaryDay
    val Anthracite = Color(0xFFDFDFDF) // SeparatorDay
    val Ash = Color(0xFFC9C5CA) // ArrowDay && GrayNight
    
    /*     NIGHT COLORS    */
    val AlmostDark = Color(0xFF1C1C1D) // ContainerNight && TextFieldNight
    val Zircon = Color(0xFF98989F) // TextSecondaryNight
    val Lead = Color(0xFF464649) // SeparatorNight
    val WetAsh = Color(0xFFCAC4D0) // ArrowNight
    val SuperDark = Color(0x1FE3E3E3) // GradientButtonNight
    
    /*     CATEGORY COLORS    */
    //                                       Day
    val Orange = Color(0xFFFF7A00)
    val Yellow = Color(0xFFFFBA1C)
    val Red = Color(0xFFE20000)
    val Blue = Color(0xFF13CCF4)
    val Cyan = Color(0xFF049FD0)
    val Purple = Color(0xFF7678DA)
    
    //                                       Night
    val DarkOrange = Color(0xFFFD8E28)
    val DarkYellow = Color(0xFFFFD800)
    val DarkRed = Color(0xFFF52626)
    val DarkBlue = Color(0xFF35D9FD)
    val DarkCyan = Color(0xFF00ADE3)
    val DarkPurple = Color(0xFF8284EC)
    
    /*     ANOTHER COLORS    */
    val ExtraLightGray = Color(0xFFDADADA)
    val Gray = Color(0xFFE9E9EA)
    val RatGray = Color(0xFF484649)
    val Border = Color(0xFFB0B0B0)
    val meetingCardBackBackgroundNight = Color(0xFF303030)
    val LightPink = Color(0x33FF4745)
    val RottenPlum = Color(0xFF49454F)
    val Pinky = Color(0xFFFFEDEB)
    val LightGreen = Color(0x3335C65A)
    val BlackerWhite = Color(0xFFF0F0F0)
    val DarkShadow = Color(0xFF212122)
    val WhiteShadow = Color(0xFFF7F7F7)
    val NickelGray = Color(0xFFE0E0E0)
    val halfTransparentlyGray = Color(0x80C4C4C4)
    val gripGrayDark = Color(0xFF767373)
    // Add new colors


    // Translations
    val thirdOpaqueGray = Color(0x305E5E5E)
    val blackSeventy = Color(0x70000000)
    val textField = Color(0xFF49454F)
}

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "MaterialTheme.colorScheme",
        "androidx.compose.material3.MaterialTheme"
    )
)

val LightColorScheme = lightColorScheme(
    outline = Anthracite, // separator
    outlineVariant = Ash, // arrow
    scrim = Ash, // gray2
    surface = YellowGrin, // green1
    onSurface = SaladGrin, //green2
    surfaceTint = LightGrin, //green 3
    surfaceVariant = OrangeRed, // red1
    inverseOnSurface = FireRed, // red2
    primary = AlmostRed, //    red
    onPrimary = DimRed, // red inactive
    secondary = Green, // green
    onSecondary = DimGrin, // green inactive
    tertiary = PreDark, //  text
    onTertiary = Silver, //  textSecondary
    background = LightGray, // background
    primaryContainer = White, // CardBack
    inversePrimary = DimRed, // darkRedInactive
    inverseSurface = DimGrin, // darkGrinInactive
    onPrimaryContainer = White //TextBox
    // Add in scheme
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "MaterialTheme.colorScheme",
        "androidx.compose.material3.MaterialTheme"
    )
)
val DarkColorScheme = darkColorScheme(
    outline = Lead, // separator
    outlineVariant = Ash, // arrow
    scrim = Ash, // gray2
    surface = YellowGrin, // green1
    onSurface = SaladGrin, //green2
    surfaceTint = LightGrin, //green 3
    surfaceVariant = OrangeRed, // red1
    inverseOnSurface = FireRed, // red2
    primary = AlmostRed, //    red
    onPrimary = DimRed, // red inactive
    secondary = Green, // green
    onSecondary = DimGrin, // green inactive
    tertiary = White, //  text
    onTertiary = Zircon, //  textSecondary
    background = Black, // background
    primaryContainer = AlmostDark, // CardBack
    inversePrimary = SuperDark, // darkRedInactive
    inverseSurface = SuperDark, // darkGrinInactive
    onPrimaryContainer = AlmostDark // TextBox
    // Add in scheme
)