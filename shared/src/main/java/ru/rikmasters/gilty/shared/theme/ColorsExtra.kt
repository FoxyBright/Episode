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
    val navBarActiveBackground: Color = Color.Unspecified,
    val navBarActive: Color = Color.Unspecified,
    val navBarInactive: Color = Color.Unspecified,
    val navBarAddButton: Color = Color.Unspecified,
    val chatBackground: Color = Color.Unspecified,
    val commentBackground: Color = Color.Unspecified,
    val priceTextFieldText: Color = Color.Unspecified,
    val tabActive: Color = Color.Unspecified,
    val tabActiveOnline: Color = Color.Unspecified,
    val tabInactive: Color = Color.Unspecified,
    val chatBack: Color = Color.Unspecified,
    val meetButtonColors: Color = Color.Unspecified,
    val meetCardShadow: Color = Color.Unspecified,
    val meetTransparencyCircleColor: Color = Color.Unspecified,
    
    // Categories Colors
    val sport: Color = Color.Unspecified,
    val business: Color = Color.Unspecified,
    val travel: Color = Color.Unspecified,
    val masterClasses: Color = Color.Unspecified,
    val entertainment: Color = Color.Unspecified,
    val erotic: Color = Color.Unspecified,
    val party: Color = Color.Unspecified,
    val art: Color = Color.Unspecified,
    // Add new color in scheme = Color.Unspecified
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "ThemeExtra.colors",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"
    )
)
val LightExtraColors = ExtraColors(
    elementsBack = Colors.White,
    chipGray = Colors.Gray,
    grayButton = Colors.Gray,
    searchCardBackground = Colors.LightGray,
    policyAgreeColor = Colors.Silver,
    mainTrackCheckBox = Colors.Ash,
    secondaryTrackCheckBox = Colors.Border,
    borderColor = Colors.White,
    lockColors = Colors.White,
    lockColorsBackground = Colors.lockColorsBackgroundDay,
    meetingCardBackBackground = Colors.White,
    navBarActiveBackground = Colors.LightPink,
    navBarActive = Colors.RottenPlum,
    navBarInactive = Colors.Silver,
    navBarAddButton = Colors.AlmostDark,
    chatBackground = Colors.Pinky,
    commentBackground = Colors.RottenPlum,
    priceTextFieldText = Colors.AlmostRed,
    tabActive = Colors.GentlePinky,
    tabInactive = Colors.LightGray,
    tabActiveOnline = Colors.LightGreen,
    chatBack = Colors.BlackerWhite,
    meetButtonColors = Colors.DoubleAddWhite,
    meetCardShadow = Colors.WhiteShadow,
    meetTransparencyCircleColor = Colors.RatGray,
    
    // Categories Colors
    sport = Colors.Orange,
    business = Colors.Blue,
    travel = Colors.Cyan,
    masterClasses = Colors.Yellow,
    entertainment = Colors.AlmostRed,
    erotic = Colors.Red,
    party = Colors.Purple,
    art = Colors.Orange,
    // Add colors in scheme from Colors-file
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "ThemeExtra.colors",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"
    )
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
    meetingCardBackBackground = Colors.meetingCardBackBackgroundNight,
    navBarActiveBackground = Colors.AlmostDark,
    navBarActive = Colors.White,
    navBarInactive = Colors.Zircon,
    navBarAddButton = Colors.White,
    chatBackground = Colors.RottenPlum,
    commentBackground = Colors.RottenPlum,
    priceTextFieldText = Colors.White,
    tabActive = Colors.RudePinky,
    tabInactive = Colors.Black,
    tabActiveOnline = Colors.LightGreen,
    chatBack = Colors.Black,
    meetButtonColors = Colors.Lead,
    meetCardShadow = Colors.DarkShadow,
    meetTransparencyCircleColor = Colors.RatGray,
    
    // Categories Colors
    sport = Colors.DarkOrange,
    business = Colors.DarkBlue,
    travel = Colors.DarkCyan,
    masterClasses = Colors.DarkYellow,
    entertainment = Colors.AlmostRed,
    erotic = Colors.DarkRed,
    party = Colors.DarkPurple,
    art = Colors.DarkOrange,
    // Add colors in scheme from Colors-file
)
