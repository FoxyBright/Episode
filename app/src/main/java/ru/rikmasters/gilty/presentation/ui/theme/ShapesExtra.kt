@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class ExtraShapes(
    val cardShape: Shape = RectangleShape,
    val smallCardShape: Shape = RectangleShape
    // Добавить новую форму в схему = RectangleShape
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.shapes", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val DefaultExtraShapes = ExtraShapes(
    cardShape = RoundedCornerShape(14.dp)
    // Добавить в схему формы
)