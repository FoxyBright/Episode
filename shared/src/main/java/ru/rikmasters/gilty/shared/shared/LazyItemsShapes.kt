package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra

@Composable
fun LazyItemsShapes(index: Int, size: Int): Shape {
    return if (size != 1)
        return when (index) {
            0 -> ThemeExtra.shapes.mediumTopRoundedShape
            size - 1 -> ThemeExtra.shapes.mediumBottomRoundedShape
            else -> RoundedCornerShape(0.dp)
        } else MaterialTheme.shapes.medium
}