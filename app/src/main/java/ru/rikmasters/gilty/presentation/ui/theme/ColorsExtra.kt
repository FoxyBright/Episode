@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val myExtraColor: Color = Color.Unspecified,
    val mainTextColor: Color = Color.Unspecified,
    val notActive: Color = Color.Unspecified,
    val secondaryTextColor: Color = Color.Unspecified,
    val cardBackground: Color = Color.Unspecified,
    val searchCardBackground: Color = Color.Unspecified,
    val grayIcon: Color = Color.Unspecified,
    val transparentBtnTextColor: Color = Color.Unspecified,
    val gradientColor1: Color = Color.Unspecified,
    val gradientColor2: Color = Color.Unspecified,
    val grayButton: Color = Color.Unspecified,
    val elementsBack: Color = Color.Unspecified,
    val primary: Color = Color.Unspecified,
    val white: Color = Color.Unspecified,
    val divider: Color = Color.Unspecified,
    val chipGray: Color = Color.Unspecified,
    val policyAgreeColor: Color = Color.Unspecified
    // Добавить новый цвет в схему = Color.Unspecified
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.colors", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val LightExtraColors = ExtraColors(
    myExtraColor = Colors.Purple40,
    elementsBack = Color.White,
    mainTextColor = Colors.PrimaryTextDay,
    secondaryTextColor = Colors.SecondaryTextDay,
    notActive = Colors.NotActiveDay,
    cardBackground = Colors.CardBackgroundDay,
    grayIcon = Colors.Gray2,
    chipGray = Colors.Gray,
    transparentBtnTextColor = Color.Black,
    gradientColor1 = Colors.Gradient1,
    gradientColor2 = Colors.Gradient2,
    grayButton = Colors.Gray,
    divider = Colors.Divider,
    searchCardBackground = Colors.searchCardBackgroundDay,
    policyAgreeColor = Colors.SecondaryTextDay


    // Добавить в схему цвета из файла Colors
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.colors", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val DarkExtraColors = ExtraColors(
    myExtraColor = Colors.Purple80,
    elementsBack = Colors.PrimaryTextDay,
    mainTextColor = Colors.PrimaryTextNight,
    secondaryTextColor = Colors.SecondaryTextNight,
    notActive = Colors.NotActiveNight,
    cardBackground = Colors.CardBackgroundNight,
    grayIcon = Colors.Gray2,
    chipGray = Colors.Gray,
    transparentBtnTextColor = Color.White,
    gradientColor1 = Colors.Gradient2,
    grayButton = Colors.Gray,
    divider = Colors.Gray2,
    searchCardBackground = Colors.searchCardBackgroundNight,
    policyAgreeColor = Colors.SecondaryTextDay
    // Добавить в схему цвета из файла Colors
)
