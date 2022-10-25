package ru.rikmasters.gilty.shared.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

@Deprecated("Надо использовать тему",
    ReplaceWith("MaterialTheme.shapes", "androidx.compose.material3.MaterialTheme"))
val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    extraSmall = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(20.dp)
    // Добавить новые формы
)