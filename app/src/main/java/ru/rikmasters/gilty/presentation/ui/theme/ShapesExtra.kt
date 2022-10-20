@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class ExtraShapes(
    val cardShape: Shape = RectangleShape,
    val smallCardShape: Shape = RectangleShape,
    val largeTopRoundedShape: Shape = RectangleShape,
    val largeBottomRoundedShape: Shape = RectangleShape,
    val mediumTopRoundedShape: Shape = RectangleShape,
    val mediumBottomRoundedShape: Shape = RectangleShape,

    // Добавить новую форму в схему = RectangleShape
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith("ThemeExtra.shapes", "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra")
)
val DefaultExtraShapes = ExtraShapes(
    cardShape = RoundedCornerShape(14.dp),
    largeTopRoundedShape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
    largeBottomRoundedShape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp),
    mediumTopRoundedShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    mediumBottomRoundedShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
// Добавить в схему формы
)