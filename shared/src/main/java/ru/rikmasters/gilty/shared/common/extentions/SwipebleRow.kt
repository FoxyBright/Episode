package ru.rikmasters.gilty.shared.common.extentions

import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Offset.Companion.Zero
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.core.util.composable.getConfiguration
import ru.rikmasters.gilty.core.util.composable.getDensity
import kotlin.math.abs

fun Modifier.swipeableRow(
    state: DragRowState,
    context: Context? = null,
    onSwiped: (RowDirection) -> Unit,
) = pointerInput(Unit) {
    coroutineScope {
        detectHorizontalDragGestures(
            onDragEnd = {
                launch { state.onDragEnd(context, onSwiped) }
            },
            onHorizontalDrag = { change, dragAmount ->
                launch { state.onHorizontalDrag(change, dragAmount) }
            },
            onDragCancel = { launch { state.reset() } }
        )
    }
}.graphicsLayer { translationX = state.offset.value.x }

private suspend fun DragRowState.onDragEnd(
    context: Context?,
    onSwiped: (RowDirection) -> Unit,
) {
    val dragOffset = abs(
        offset.targetValue.coerceIn(maxWidth).x
    )
    if(dragOffset < maxWidth / 4) reset()
    else {
        context?.let { vibrate(it) }
        if(offset.targetValue.x < 0) {
            swipe(RowDirection())
            reset()
            onSwiped(RowDirection())
        }
    }
}

private suspend fun DragRowState.onHorizontalDrag(
    change: PointerInputChange,
    dragAmount: Float,
) {
    if(change.positionChange() != Zero)
        change.consume()
    drag(
        (offset.targetValue.x + dragAmount)
            .coerceIn(-maxWidth, 0f)
    )
}

fun Offset.coerceIn(maxWidth: Float) =
    copy(x.coerceIn(-maxWidth, 0f))

class RowDirection

@Composable
fun rememberDragRowState(
    density: Density = getDensity(),
    configuration: Configuration = getConfiguration(),
) = remember {
    DragRowState(with(density) {
        configuration.screenWidthDp.dp.toPx()
    })
}

class DragRowState(val maxWidth: Float = 0f) {
    
    val offset =
        Animatable(offset(0f), Offset.VectorConverter)
    
    private var swipedDirection: RowDirection? by
    mutableStateOf(null)
    
    suspend fun reset() {
        offset.animateTo(
            offset(0f),
            tween(400)
        )
    }
    
    suspend fun swipe(direction: RowDirection) {
        if(direction == RowDirection())
            offset.animateTo(
                offset(-(maxWidth * 1.5f)),
                tween(400)
            )
        this.swipedDirection = direction
    }
    
    private fun offset(
        x: Float = offset.value.x,
    ) = Offset(x, 0f)
    
    suspend fun drag(x: Float) {
        offset.animateTo(offset(x))
    }
}