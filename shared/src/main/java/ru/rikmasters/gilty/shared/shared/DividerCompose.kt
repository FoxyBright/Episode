package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GDivider(
    modifier: Modifier = Modifier,
    color: Color = if(isSystemInDarkTheme())
        Color(0xFFDFDFDF)
    else Color(0xFF464649),
) {
    Divider(modifier, 1.dp, color)
}