@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.shared.theme.Colors.AlmostDark
import ru.rikmasters.gilty.shared.theme.Colors.AlmostRed
import ru.rikmasters.gilty.shared.theme.Colors.Anthracite
import ru.rikmasters.gilty.shared.theme.Colors.Ash
import ru.rikmasters.gilty.shared.theme.Colors.Asphalt
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
import ru.rikmasters.gilty.shared.theme.Colors.Twilight
import ru.rikmasters.gilty.shared.theme.Colors.White
import ru.rikmasters.gilty.shared.theme.Colors.YellowGrin
import ru.rikmasters.gilty.shared.theme.Colors.Zircon


@Deprecated(
    "Надо использовать тему",
    ReplaceWith("MaterialTheme.colorScheme", "androidx.compose.material3.MaterialTheme")
)

@Suppress("unused")
object Colors {
    /*     UNIVERSAL COLORS    */
    val White = Color(0xFFFFFFFF) // ContainerDay && TextNight
    val Black = Color(0xFF000000) // ContainerDay && TextNight
    val AlmostRed = Color(0xFFFF4745) // PrimaryRed
    val Green = Color(0xFF35C65A) // PrimaryGreen
    val YellowGrin = Color(0xFFBCF028) // GreenGradient1
    val SaladGrin = Color(0xFF8DE139) // GreenGradient2
    val LightGrin = Color(0xFF34C559) // GreenGradient3
    val DimGrin = Color(0xFF88DB9E) // GreenInactive
    val OrangeRed = Color(0xFFFF6645) // RedGradient1
    val FireRed = Color(0xFFDF2B4B) // RedGradient2
    val DimRed = Color(0xFFFD7C7A) // RedInactive

    /*     DAY COLORS    */
    val LightGray = Color(0xFFF6F6F6) // BackgroundDay
    val PreDark = Color(0xFF1C1B1F) // TextDay
    val Silver = Color(0xFFAEAAAE) // TextSecondaryDay
    val Anthracite = Color(0xFFDFDFDF) // SeparatorDay
    val Ash = Color(0xFFC9C5CA) // ArrowDay

    /*     NIGHT COLORS    */
    val AlmostDark = Color(0xFF1C1C1D) // ContainerNight
    val Zircon = Color(0xFF98989F) // TextSecondaryNight
    val Lead = Color(0xFF464649) // SeparatorNight
    val WetAsh = Color(0xFFCAC4D0) // ArrowNight
    val Asphalt = Color(0xFF767373) // GrayNight
    val SuperDark = Color(0xFF1B1B1B) // GradientButtonNight
    val Twilight = Color(0xFF353535) // TextFieldNight

    /*     CATEGORY COLORS    */
    //                                       Day
    val Orange = Color(0xFFFF9500)
    val Yellow = Color(0xFFFFB800)
    val Red = Color(0xFFC60000)
    val Blue = Color(0xFF33A9FE)
    val Cyan = Color(0xFF00B8F2)
    val Purple = Color(0xFF6061B8)

    //                                       Night
    val DarkOrange = Color(0xFFFF9F0A)
    val DarkYellow = Color(0xFFFFC42C)
    val DarkRed = Color(0xFFFF0000)
    val DarkBlue = Color(0xFF2DC2FF)
    val DarkCyan = Color(0xFF29B2FF)
    val DarkPurple = Color(0xFF5E5CE6)

    /*     ANOTHER COLORS    */
    val Gray = Color(0xFFE9E9EA)
    val Border = Color(0xFFB0B0B0)
    val lockColors = Color(0xFF787579)
    val lockColorsBackgroundDay = Color(0xFFB3B3B3)
    val meetingCardBackBackgroundNight = Color(0xFF303030)
    val lockColorsBackgroundNight = Color(0xFF222222)
    val LightPink = Color(0xFFFFDADA)
    val RottenPlum = Color(0xFF49454F)
    val Pinky = Color(0xFFF6EDEC)
    val GentlePinky = Color(0xFFF8D3D3)
    val RudePinky = Color(0xFF330E0E)
    val AdditionalWhite = Color(0xFFFEFEFE)
    // Add new colors
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
    scrim = Asphalt, // gray2
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
    onPrimaryContainer = Twilight // TextBox
    // Add in scheme
)