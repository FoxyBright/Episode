package ru.rikmasters.gilty.addmeet.presentation.ui.extentions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun DashesPreview() {
    Dashes(4, 1, Modifier.padding(16.dp))
}

@Composable
fun Dashes(
    count: Int,
    active: Int,
    modifier: Modifier = Modifier,
    color: Color = colorScheme.primary
) {
    Row(modifier, Center, CenterVertically) {
        repeat(count) { Item(it == active, color) }
    }
}

@Composable
private fun Item(active: Boolean, color: Color) {
    Box(
        Modifier
            .padding(horizontal = 2.dp)
            .width(if(active) 24.dp else 12.dp)
            .height(6.dp)
            .background(
                if(active) color
                else colorScheme.outline,
                CircleShape
            )
    )
}