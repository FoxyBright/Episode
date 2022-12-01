package ru.rikmasters.gilty.shared.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.shapes

@Composable
fun LazyItemsShapes(index: Int, size: Int): Shape {
    return if (size != 1)
        return when (index) {
            0 -> shapes.mediumTopRoundedShape
            size - 1 -> shapes.mediumBottomRoundedShape
            else -> shapes.zero
        } else MaterialTheme.shapes.medium
}

@Composable
fun LazyRowItemsShapes(index: Int, size: Int): Shape {
    return if (size != 1)
        return when (index) {
            0 -> shapes.extraLargeStartRoundedShape
            size - 1 -> shapes.extraLargeEndRoundedShape
            else -> shapes.zero
        } else MaterialTheme.shapes.medium
}