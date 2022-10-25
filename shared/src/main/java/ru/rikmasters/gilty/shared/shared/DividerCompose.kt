package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Divider(modifier, 1.dp, ThemeExtra.colors.divider)
}

@Composable
fun DividerBold(modifier: Modifier = Modifier) {
    Divider(modifier, 5.dp, ThemeExtra.colors.divider)
}