package ru.rikmasters.gilty.gallery.cropper.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.gallery.cropper.crop
import ru.rikmasters.gilty.gallery.cropper.model.AspectRatio
import ru.rikmasters.gilty.gallery.cropper.model.CropOutline
import ru.rikmasters.gilty.gallery.cropper.model.OutlineType
import ru.rikmasters.gilty.gallery.cropper.state.CropState

object CropDefaults {
    
    /**
     * Properties effect crop behavior that should be passed to [CropState]
     */
    
    /**
     * Style is cosmetic changes that don't effect how [CropState] behaves because of that
     * none of these properties are passed to [CropState]
     */
    fun style(): CropStyle {
        return CropStyle(
            drawOverlay = false,
            drawGrid = false,
            strokeWidth = 2.dp,
            overlayColor = Color.Gray,
            handleColor = Color.White,
            backgroundColor = Color(0x99000000)
        )
    }
}

/**
 * Data class for selecting cropper properties. Fields of this class control inner work
 * of [CropState] while some such as [cropType], [aspectRatio], [handleSize]
 * is shared between ui and state.
 */
@Immutable
data class CropProperties(
    val cropType: CropType,
    val handleSize: Float,
    val contentScale: ContentScale,
    val cropOutlineProperty: CropOutlineProperty,
    val aspectRatio: AspectRatio,
    val overlayRatio: Float,
    val pannable: Boolean,
    val fling: Boolean,
    val rotatable: Boolean,
    val zoomable: Boolean,
    val maxZoom: Float,
    val minZoom: Float = 0.5f,
)

/**
 * Data class for cropper styling only. None of the properties of this class is used
 * by [CropState] or [Modifier.crop]
 */
@Immutable
data class CropStyle(
    val drawOverlay: Boolean,
    val drawGrid: Boolean,
    val strokeWidth: Dp,
    val overlayColor: Color,
    val handleColor: Color,
    val backgroundColor: Color,
    val cropTheme: CropTheme = CropTheme.System,
)

@Immutable
data class CropOutlineProperty(
    val outlineType: OutlineType,
    val cropOutline: CropOutline,
)

/**
 * Light, Dark or system controlled theme
 */
enum class CropTheme { System }
