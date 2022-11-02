@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val searchCardBackground: Color = Color.Unspecified,
    val grayButton: Color = Color.Unspecified,
    val elementsBack: Color = Color.Unspecified,
    val chipGray: Color = Color.Unspecified,
    val policyAgreeColor: Color = Color.Unspecified,
    val borderColor: Color = Color.Unspecified,
    val mainTrackCheckBox: Color = Color.Unspecified,
    val secondaryTrackCheckBox: Color = Color.Unspecified,
    val meetingCardBackBackground: Color = Color.Unspecified,
    val lockColors: Color = Color.Unspecified,
    val lockColorsBackground: Color = Color.Unspecified,
    // Add new color in scheme = Color.Unspecified
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith("ThemeExtra.colors",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra")
)
val LightExtraColors = ExtraColors(
    elementsBack = Color.White,
    chipGray = Colors.Gray,
    grayButton = Colors.Gray,
    searchCardBackground = Colors.LightGray,
    policyAgreeColor = Colors.Silver,
    mainTrackCheckBox = Colors.Ash,
    secondaryTrackCheckBox = Colors.Border,
    borderColor = Color.White,
    lockColors = Color.White,
    lockColorsBackground = Colors.lockColorsBackgroundDay,
    meetingCardBackBackground = Color.White
    // Add colors in scheme from Colors-file
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith("ThemeExtra.colors",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra")
)
val DarkExtraColors = ExtraColors(
    elementsBack = Colors.PreDark,
    chipGray = Colors.Gray,
    grayButton = Colors.Gray,
    searchCardBackground = Colors.SuperDark,
    policyAgreeColor = Colors.Silver,
    mainTrackCheckBox = Colors.Border,
    secondaryTrackCheckBox = Colors.Ash,
    borderColor = Colors.meetingCardBackBackgroundNight,
    lockColors = Colors.lockColors,
    lockColorsBackground = Colors.lockColorsBackgroundNight,
    meetingCardBackBackground = Colors.meetingCardBackBackgroundNight
    // Add colors in scheme from Colors-file
)
