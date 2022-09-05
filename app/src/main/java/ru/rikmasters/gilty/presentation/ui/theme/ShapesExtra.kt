@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class ExtraShapes(
    val myExtraShape: Shape = RectangleShape
    // Добавить новую форму в схему = RectangleShape
)

@Deprecated("Надо использовать тему",
    ReplaceWith("ThemeExtra.shapes", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"))
val DefaultExtraShapes = ExtraShapes(
    myExtraShape = CutCornerShape(15.dp)
    // Добавить в схему формы
)