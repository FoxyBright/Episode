package ru.rikmasters.gilty.gallery.cropper.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape

/**
 * Model for drawing title with shape for crop selection menu. Aspect ratio is used
 * for setting overlay in state and UI
 */
@Immutable
data class CropAspectRatio(
    val title: String,
    val shape: Shape,
    val aspectRatio: AspectRatio = AspectRatio.Unspecified,
    val icons: List<Int> = listOf()
)

/**
 * Value class for containing aspect ratio
 * and [AspectRatio.Unspecified] for comparing
 */
@Immutable
data class AspectRatio(val value: Float) {
    companion object {
        val Unspecified = AspectRatio(-1f)
    }
}