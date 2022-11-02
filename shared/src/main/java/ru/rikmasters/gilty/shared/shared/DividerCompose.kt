package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Divider(modifier, 1.dp, MaterialTheme.colorScheme.outline)
}

@Composable
fun DividerBold(modifier: Modifier = Modifier) {
    Divider(modifier, 5.dp, MaterialTheme.colorScheme.outline)
}