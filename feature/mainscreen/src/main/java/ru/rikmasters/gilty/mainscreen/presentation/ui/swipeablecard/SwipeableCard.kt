package ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Offset.Companion.Zero
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import kotlin.math.abs

fun Modifier.swipeableCard(
    onSwiped: (DirectionType) -> Unit,
    state: SwipeableCardState,
) = pointerInput(Unit) {
    detectDrag(
        state = state,
        onSwiped = onSwiped
    )
}.graphicsLayer {
    translationX = state.offset.value.x
    translationY = state.offset.value.y
    rotationZ = (state.offset.value.x / 60)
        .coerceIn(-40f, 40f)
}

private suspend fun PointerInputScope.detectDrag(
    state: SwipeableCardState,
    onSwiped: (DirectionType) -> Unit,
) {
    coroutineScope {
        detectDragGestures(
            onDragEnd = {
                launch { onDragEnd(state, onSwiped) }
            },
            onDrag = { change, amount ->
                launch { onDrag(change, state, amount) }
            },
            onDragCancel = {
                launch { state.reset() }
            }
        )
    }
}

private suspend fun onDragEnd(
    state: SwipeableCardState,
    onSwiped: (DirectionType) -> Unit,
) {
    when {
        state.notEnoughToPull() -> state.reset()
        state.toSwipe() -> state.swipe(
            state.getDirection(), onSwiped
        )
        else -> state.reset()
    }
}

private fun SwipeableCardState.getDirection() =
    if(offset.targetValue.x > 0) RIGHT else LEFT

private fun SwipeableCardState.toSwipe() =
    abs(offset.targetValue.x) > abs(offset.targetValue.y)

private fun SwipeableCardState.notEnoughToPull(
) = Offset(offset.targetValue.x.coerceIn(-maxWidth, maxWidth), 0f)
    .let { abs(it.x) < maxWidth / 4 && abs(it.y) < maxHeight / 4 }

private suspend fun onDrag(
    change: PointerInputChange,
    state: SwipeableCardState,
    amount: Offset,
) {
    val newValue = state
        .offset
        .targetValue
        .plus(amount)
        .let { state getOffset it }
    
    if(change.positionChange() != Zero)
        change.consume()
    
    state.drag(newValue.x, newValue.y)
}

private infix fun SwipeableCardState.getOffset(
    offset: Offset,
) = Offset(
    offset.x.coerceIn(-maxWidth, maxWidth),
    offset.y.coerceIn(-maxHeight, maxHeight)
)

private suspend fun SwipeableCardState.swipe(
    direction: DirectionType,
    onSwiped: (DirectionType) -> Unit,
) {
    swipe(direction)
    onSwiped(direction)
}