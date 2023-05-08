package ru.rikmasters.gilty.gallery.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class CornerRadiusProperties(
    val topStartPercent: Int = 14,
    val topEndPercent: Int = 14,
    val bottomStartPercent: Int = 14,
    val bottomEndPercent: Int = 14
)

@Immutable
data class PolygonProperties(
    val sides: Int = 6,
    val angle: Float = 0f,
    val offset: Offset = Offset.Zero
)

@Immutable
data class OvalProperties(
    val startAngle: Float = 0f,
    val sweepAngle: Float = 360f,
    val offset: Offset = Offset.Zero
)
