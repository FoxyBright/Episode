package ru.rikmasters.gilty.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GBottomSheetBackground(
    content: @Composable () -> Unit,
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = cardColors(colorScheme.background),
        shape = shapes.extraLarge.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )
    ) { content() }
}