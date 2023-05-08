package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Composable
fun lazyItemsShapes(
    index: Int, size: Int,
    radius: Dp = 12.dp,
) = if(size == 1)
    RoundedCornerShape(radius)
else when(index) {
    0 -> RoundedCornerShape(
        topStart = radius,
        topEnd = radius
    )
    
    size - 1 -> RoundedCornerShape(
        bottomStart = radius,
        bottomEnd = radius
    )
    
    else -> shapes.zero
}

@Composable
@Suppress("unused")
fun lazyRowItemsShapes(
    index: Int, size: Int,
) = if(size == 1)
    MaterialTheme.shapes.medium
else when(index) {
    0 -> shapes.extraLargeStartRoundedShape
    size - 1 -> shapes.extraLargeEndRoundedShape
    else -> shapes.zero
}
