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
    val meetCloseCircleColor: Color = Color.Unspecified,
    val meetCloseCrossColor: Color = Color.Unspecified,
    val meetCardPlaceHolder: Color = Color.Unspecified,
    val notificationCloud: Color = Color.Unspecified,
    val meetingTransparencyShape: Color = Color.Unspecified,

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


    //Translations Colors (always the same)
    val white: Color = Color.Unspecified,
    val thirdOpaqueGray: Color = Color.Unspecified,
    val bottomSheetGray: Color = Color.Unspecified,
    val blackSeventy: Color = Color.Unspecified,
    val mainCard: Color = Color.Unspecified,
    val mainNightGreen: Color = Color.Unspecified,
    val mainDayGreen: Color = Color.Unspecified,
    val mainNotActiveGreen: Color = Color.Unspecified,
    val textField: Color = Color.Unspecified,
    val preDarkColor: Color = Color.Unspecified,
    val zirkon: Color = Color.Unspecified,
    val messageBar: Color = Color.Unspecified

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
    meetCloseCircleColor = Colors.White,
    meetCloseCrossColor = Colors.RatGray,
    meetCardPlaceHolder = Colors.NickelGray,
    notificationCloud = Colors.Gray,
    meetingTransparencyShape = Colors.halfTransparentlyGray,

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

    //Translations Colors (always the same)
    white = Colors.White,
    thirdOpaqueGray = Colors.thirdOpaqueGray,
    bottomSheetGray = Colors.bottomSheetGray,
    blackSeventy = Colors.blackSeventy,
    mainCard = Colors.mainCard,
    mainNightGreen = Colors.mainGreen,
    mainDayGreen = Colors.mainGreen,
    mainNotActiveGreen = Colors.greenInactive,
    textField = Colors.textField,
    preDarkColor = Colors.PreDark,
    zirkon = Colors.Zircon,
    messageBar = Colors.RatGray
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
    navBarActiveBackground = Colors.LightPink,
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
    meetCloseCircleColor = Colors.RatGray,
    meetCloseCrossColor = Colors.White,
    meetCardPlaceHolder = Colors.NickelDarkGray,
    notificationCloud = Colors.NickelDarkGray,
    meetingTransparencyShape = Colors.halfTransparentlyGray,

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


    //Translations Colors (always the same)
    white = Colors.White,
    thirdOpaqueGray = Colors.thirdOpaqueGray,
    bottomSheetGray = Colors.bottomSheetGray,
    blackSeventy = Colors.blackSeventy,
    mainCard = Colors.mainCard,
    mainNightGreen = Colors.mainGreen,
    mainDayGreen = Colors.mainGreen,
    mainNotActiveGreen = Colors.greenInactive,
    textField = Colors.textField,
    preDarkColor = Colors.PreDark,
    zirkon = Colors.Zircon,
    messageBar = Colors.RatGray

)
