package ru.rikmasters.gilty.presentation.ui.shared

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldColors() =  TextFieldDefaults.textFieldColors(
    containerColor = ThemeExtra.colors.cardBackground,
    unfocusedIndicatorColor = Color.Transparent,
    unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
    focusedLabelColor = ThemeExtra.colors.mainTextColor,
    focusedIndicatorColor = Color.Transparent
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentTextFieldColors() =  TextFieldDefaults.textFieldColors(
    containerColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    unfocusedLabelColor = ThemeExtra.colors.secondaryTextColor,
    focusedLabelColor = ThemeExtra.colors.mainTextColor,
    focusedIndicatorColor = Color.Transparent
)