package ru.rikmasters.gilty.shared.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class ExtraShapes(
    val cardShape: Shape = RectangleShape,
    val zero: Shape = RectangleShape,
    val smallCardShape: Shape = RectangleShape,
    val largeTopRoundedShape: Shape = RectangleShape,
    val largeBottomRoundedShape: Shape = RectangleShape,
    val mediumTopRoundedShape: Shape = RectangleShape,
    val mediumBottomRoundedShape: Shape = RectangleShape,
    val ExtraLargeStartRoundedShape: Shape = RectangleShape,
    val ExtraLargeEndRoundedShape: Shape = RectangleShape,
    val ultraTopRoundedShape: Shape = RectangleShape,
    val chatRoundedShape: Shape = RectangleShape
    // Add new form in scheme = RectangleShape
)

@Deprecated(
    "Надо использовать тему",
    ReplaceWith(
        "ThemeExtra.shapes",
        "ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra"
    )
)

val DefaultExtraShapes = ExtraShapes(
    cardShape = RoundedCornerShape(14.dp),
    largeTopRoundedShape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
    largeBottomRoundedShape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp),
    mediumTopRoundedShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    mediumBottomRoundedShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
    ultraTopRoundedShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ExtraLargeStartRoundedShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
    ExtraLargeEndRoundedShape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp),
    chatRoundedShape = RoundedCornerShape(30.dp),
    zero = RoundedCornerShape(0.dp)
// Add form in scheme
)