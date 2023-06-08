@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import ru.rikmasters.gilty.shared.theme.Colors.AlmostDark
import ru.rikmasters.gilty.shared.theme.Colors.AlmostRed
import ru.rikmasters.gilty.shared.theme.Colors.Ash
import ru.rikmasters.gilty.shared.theme.Colors.Black
import ru.rikmasters.gilty.shared.theme.Colors.BlackerWhite
import ru.rikmasters.gilty.shared.theme.Colors.Blue
import ru.rikmasters.gilty.shared.theme.Colors.Border
import ru.rikmasters.gilty.shared.theme.Colors.Cyan
import ru.rikmasters.gilty.shared.theme.Colors.DarkBlue
import ru.rikmasters.gilty.shared.theme.Colors.DarkCyan
import ru.rikmasters.gilty.shared.theme.Colors.DarkOrange
import ru.rikmasters.gilty.shared.theme.Colors.DarkPurple
import ru.rikmasters.gilty.shared.theme.Colors.DarkRed
import ru.rikmasters.gilty.shared.theme.Colors.DarkShadow
import ru.rikmasters.gilty.shared.theme.Colors.DarkYellow
import ru.rikmasters.gilty.shared.theme.Colors.ExtraLightGray
import ru.rikmasters.gilty.shared.theme.Colors.Gray
import ru.rikmasters.gilty.shared.theme.Colors.Lead
import ru.rikmasters.gilty.shared.theme.Colors.LightGray
import ru.rikmasters.gilty.shared.theme.Colors.LightGreen
import ru.rikmasters.gilty.shared.theme.Colors.LightPink
import ru.rikmasters.gilty.shared.theme.Colors.NickelGray
import ru.rikmasters.gilty.shared.theme.Colors.Orange
import ru.rikmasters.gilty.shared.theme.Colors.Pinky
import ru.rikmasters.gilty.shared.theme.Colors.PreDark
import ru.rikmasters.gilty.shared.theme.Colors.Purple
import ru.rikmasters.gilty.shared.theme.Colors.RatGray
import ru.rikmasters.gilty.shared.theme.Colors.Red
import ru.rikmasters.gilty.shared.theme.Colors.RottenPlum
import ru.rikmasters.gilty.shared.theme.Colors.Silver
import ru.rikmasters.gilty.shared.theme.Colors.SuperDark
import ru.rikmasters.gilty.shared.theme.Colors.White
import ru.rikmasters.gilty.shared.theme.Colors.WhiteShadow
import ru.rikmasters.gilty.shared.theme.Colors.Yellow
import ru.rikmasters.gilty.shared.theme.Colors.Zircon
import ru.rikmasters.gilty.shared.theme.Colors.gripGrayDark
import ru.rikmasters.gilty.shared.theme.Colors.halfTransparentlyGray
import ru.rikmasters.gilty.shared.theme.Colors.meetingCardBackBackgroundNight
import ru.rikmasters.gilty.shared.theme.Colors.textField

data class ExtraColors(
    val searchCardBackground: Color = Unspecified,
    val grayButton: Color = Unspecified,
    val elementsBack: Color = Unspecified,
    val chipGray: Color = Unspecified,
    val policyAgreeColor: Color = Unspecified,
    val borderColor: Color = Unspecified,
    val mainTrackCheckBox: Color = Unspecified,
    val secondaryTrackCheckBox: Color = Unspecified,
    val meetingCardBackBackground: Color = Unspecified,
    val navBarActiveBackground: Color = Unspecified,
    val navBarActive: Color = Unspecified,
    val navBarInactive: Color = Unspecified,
    val navBarAddButton: Color = Unspecified,
    val chatBackground: Color = Unspecified,
    val commentBackground: Color = Unspecified,
    val priceTextFieldText: Color = Unspecified,
    val tabActive: Color = Unspecified,
    val tabActiveOnline: Color = Unspecified,
    val tabInactive: Color = Unspecified,
    val chatBack: Color = Unspecified,
    val meetButtonColors: Color = Unspecified,
    val meetCardShadow: Color = Unspecified,
    val meetCloseCircleColor: Color = Unspecified,
    val meetCloseCrossColor: Color = Unspecified,
    val meetCardPlaceHolder: Color = Unspecified,
    val notificationCloud: Color = Unspecified,
    val meetingTransparencyShape: Color = Unspecified,
    val gripColor: Color = Unspecified,
    val miniCategoriesBackground: Color = Unspecified,
    // Categories Colors
    val sport: Color = Unspecified,
    val business: Color = Unspecified,
    val travel: Color = Unspecified,
    val masterClasses: Color = Unspecified,
    val entertainment: Color = Unspecified,
    val erotic: Color = Unspecified,
    val party: Color = Unspecified,
    val art: Color = Unspecified,
    val galleryCircle: Color = Unspecified,
    // Add new color in scheme = Color.Unspecified
    
    
    //Translations Colors (always the same)
    val white: Color = Unspecified,
    val thirdOpaqueGray: Color = Unspecified,
    val bottomSheetGray: Color = Unspecified,
    val blackSeventy: Color = Unspecified,
    val mainCard: Color = Unspecified,
    val mainNightGreen: Color = Unspecified,
    val mainDayGreen: Color = Unspecified,
    val mainNotActiveGreen: Color = Unspecified,
    val textField: Color = Unspecified,
    val preDarkColor: Color = Unspecified,
    val zirkon: Color = Unspecified,
    val messageBar: Color = Unspecified,
    )

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "ThemeExtra.colors",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"
    )
)
val LightExtraColors = ExtraColors(
    elementsBack = White,
    chipGray = Gray,
    grayButton = Gray,
    searchCardBackground = LightGray,
    policyAgreeColor = Silver,
    mainTrackCheckBox = Ash,
    secondaryTrackCheckBox = Border,
    borderColor = White,
    meetingCardBackBackground = White,
    navBarActiveBackground = LightPink,
    navBarActive = RottenPlum,
    navBarInactive = Silver,
    navBarAddButton = AlmostDark,
    chatBackground = Pinky,
    commentBackground = RottenPlum,
    priceTextFieldText = AlmostRed,
    tabActive = LightPink,
    tabInactive = LightGray,
    tabActiveOnline = LightGreen,
    chatBack = BlackerWhite,
    meetButtonColors = Gray,
    meetCardShadow = WhiteShadow,
    meetCloseCircleColor = White,
    meetCloseCrossColor = AlmostDark,
    meetCardPlaceHolder = NickelGray,
    notificationCloud = Gray,
    meetingTransparencyShape = halfTransparentlyGray,
    gripColor = Ash,
    miniCategoriesBackground = LightGray,
    galleryCircle = ExtraLightGray,
    
    // Categories Colors
    sport = Orange,
    business = Blue,
    travel = Cyan,
    masterClasses = Yellow,
    entertainment = AlmostRed,
    erotic = Red,
    party = Purple,
    art = Orange,
    // Add colors in scheme from Colors-file
    
    //Translations Colors (always the same)
    white = White,
    thirdOpaqueGray = Colors.thirdOpaqueGray,
    bottomSheetGray = Silver,
    blackSeventy = Colors.blackSeventy,
    mainCard = AlmostDark,
    mainNightGreen = Colors.Green,
    mainDayGreen = Colors.Green,
    mainNotActiveGreen = Colors.DimGrin,
    textField = RottenPlum,
    preDarkColor = PreDark,
    zirkon = Zircon,
    messageBar = textField
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "ThemeExtra.colors",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"
    )
)
val DarkExtraColors = ExtraColors(
    elementsBack = PreDark,
    chipGray = RatGray,
    grayButton = RatGray,
    searchCardBackground = SuperDark,
    policyAgreeColor = Silver,
    mainTrackCheckBox = Border,
    secondaryTrackCheckBox = Ash,
    borderColor = meetingCardBackBackgroundNight,
    meetingCardBackBackground = meetingCardBackBackgroundNight,
    navBarActiveBackground = LightPink,
    navBarActive = White,
    navBarInactive = Zircon,
    navBarAddButton = White,
    chatBackground = RottenPlum,
    commentBackground = RottenPlum,
    priceTextFieldText = White,
    tabActive = LightPink,
    tabInactive = Black,
    tabActiveOnline = LightGreen,
    chatBack = Black,
    meetButtonColors = Lead,
    meetCardShadow = DarkShadow,
    meetCloseCircleColor = AlmostDark,
    meetCloseCrossColor = RottenPlum,
    meetCardPlaceHolder = Gray,
    notificationCloud = RatGray,
    meetingTransparencyShape = halfTransparentlyGray,
    gripColor = gripGrayDark,
    miniCategoriesBackground = Lead,
    galleryCircle = Black,
    
    // Categories Colors
    sport = DarkOrange,
    business = DarkBlue,
    travel = DarkCyan,
    masterClasses = DarkYellow,
    entertainment = AlmostRed,
    erotic = DarkRed,
    party = DarkPurple,
    art = DarkOrange,
    // Add colors in scheme from Colors-file
    
    
    //Translations Colors (always the same)
    white = White,
    thirdOpaqueGray = Colors.thirdOpaqueGray,
    bottomSheetGray = Silver,
    blackSeventy = Colors.blackSeventy,
    mainCard = AlmostDark,
    mainNightGreen = Colors.Green,
    mainDayGreen = Colors.Green,
    mainNotActiveGreen = Colors.DimGrin,
    textField = RottenPlum,
    preDarkColor = PreDark,
    zirkon = Zircon,
    messageBar = textField

)
