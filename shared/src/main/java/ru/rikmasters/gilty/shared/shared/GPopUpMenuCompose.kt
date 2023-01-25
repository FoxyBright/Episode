package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun GPopUpMenu(
    menuState: Boolean,
    collapse: () -> Unit,
    items: List<Triple<String, Color, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    if(menuState) Box(modifier) {
        Popup(onDismissRequest = { collapse() }) {
            Box(Modifier.shadow(1.dp, shapes.large)) {
                Column(
                    Modifier.background(
                        colorScheme.primaryContainer,
                        shapes.large
                    )
                ) {
                    items.forEachIndexed { i, it ->
                        Item(
                            i, it.first, items.size,
                            it.second, Modifier, it.third
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(
    index: Int, text: String,
    size: Int, color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth(0.6f)
            .clip(
                lazyItemsShapes(
                    index, size, 14.dp
                )
            )
            .clickable { onClick() }) {
        Text(
            text, Modifier
                .padding(horizontal = 16.dp)
                .padding(
                    top = if(index == 0)
                        24.dp else 16.dp,
                    bottom = if(index == size - 1)
                        24.dp else 16.dp
                ), color,
            style = typography.bodyMedium
        )
    }
}