package ru.rikmasters.gilty.shared.common.extentions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.swipeableRow(
    state: DragRowState,
    onSwiped: (RowDirection) -> Unit,
) = pointerInput(Unit) {
    coroutineScope {
        detectDragGestures({}, {
            launch {
                val coercedOffset = state.offset.targetValue.coerceIn(state.maxWidth)
                if (abs(coercedOffset.x) < state.maxWidth / 4) state.reset()
                else {
                    if (state.offset.targetValue.x < 0) {
                        state.swipe(RowDirection()); state.reset(); onSwiped(RowDirection())
                    }
                }
            }
        }, { launch { state.reset() } }, { change, dragAmount ->
            launch {
                if (change.positionChange() != Offset.Zero) change.consume()
                state.drag((state.offset.targetValue + dragAmount).x.coerceIn(-state.maxWidth, 0f))
            }
        }
        )
    }
}.graphicsLayer { translationX = state.offset.value.x }

fun Offset.coerceIn(maxWidth: Float): Offset {
    return copy(x.coerceIn(-maxWidth, 0f))
}

class RowDirection

@Composable
fun rememberDragRowState(): DragRowState {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    return remember { DragRowState(screenWidth) }
}

class DragRowState(val maxWidth: Float) {
    val offset = Animatable(offset(0f), Offset.VectorConverter)
    private var swipedDirection: RowDirection? by mutableStateOf(null)
    suspend fun reset() {
        offset.animateTo(offset(0f), tween(400))
    }

    suspend fun swipe(direction: RowDirection) {
        if (direction == RowDirection())
            offset.animateTo(offset(-(maxWidth * 1.5f)), tween(400))
        this.swipedDirection = direction
    }

    private fun offset(x: Float = offset.value.x): Offset {
        return Offset(x, 0f)
    }

    suspend fun drag(x: Float) {
        offset.animateTo(offset(x))
    }
}