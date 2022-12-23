package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Composable
fun LazyItemsShapes(
    index: Int, size: Int,
    radius: Dp = 12.dp
): Shape {
    return if(size != 1)
        return when(index) {
            0 -> RoundedCornerShape(
                topStart = radius,
                topEnd = radius
            )
            
            size - 1 -> RoundedCornerShape(
                bottomStart = radius,
                bottomEnd = radius
            )
            
            else -> shapes.zero
        } else RoundedCornerShape(radius)
}

@Composable
@Suppress("unused")
fun LazyRowItemsShapes(
    index: Int, size: Int,
): Shape {
    return if(size != 1)
        return when(index) {
            0 -> shapes.extraLargeStartRoundedShape
            size - 1 -> shapes.extraLargeEndRoundedShape
            else -> shapes.zero
        } else MaterialTheme.shapes.medium
}