package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GDivider(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.outline,
) { Divider(modifier, 1.dp, color) }

@Composable
fun GDividerBold(modifier: Modifier = Modifier) {
    Divider(modifier, 5.dp, colorScheme.outline)
}