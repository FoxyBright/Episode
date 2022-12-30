package ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Offset.Companion.Zero
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.*
import kotlin.math.abs

fun Modifier.swipeableCard(
    onSwiped: (DirectionType) -> Unit,
    state: SwipeableCardState,
    onSwipeCancel: () -> Unit = {},
    blockedDirections: List<DirectionType> =
        listOf(UP, DOWN),
) = pointerInput(Unit) {
    coroutineScope {
        detectDragGestures(
            onDragEnd = {
                launch {
                    val coercedOffset = state.offset.targetValue
                        .coerceIn(blockedDirections, state.maxHeight, state.maxWidth)
                    if(hasNotTravelledEnough(state, coercedOffset)) {
                        state.reset(); onSwipeCancel()
                    } else {
                        val horizontalTravel = abs(state.offset.targetValue.x)
                        val verticalTravel = abs(state.offset.targetValue.y)
                        if(horizontalTravel > verticalTravel) {
                            if(state.offset.targetValue.x > 0) {
                                state.swipe(RIGHT)
                                onSwiped(RIGHT)
                            } else {
                                state.swipe(LEFT)
                                onSwiped(LEFT)
                            }
                        } else {
                            state.reset(); onSwipeCancel()
                        }
                    }
                }
            },
            onDrag = { change, dragAmount ->
                launch {
                    val summed = state.offset.targetValue + dragAmount
                    val newValue = Offset(
                        summed.x.coerceIn(-state.maxWidth, state.maxWidth),
                        summed.y.coerceIn(-state.maxHeight, state.maxHeight)
                    )
                    if(change.positionChange() != Zero) change.consume()
                    state.drag(newValue.x, newValue.y)
                }
            },
            onDragCancel = {
                launch {
                    state.reset()
                    onSwipeCancel()
                }
            }
        )
    }
}.graphicsLayer {
    translationX = state.offset.value.x
    translationY = state.offset.value.y
    rotationZ = (state.offset.value.x / 60).coerceIn(-40f, 40f)
}

private fun Offset.coerceIn(
    blockedDirections: List<DirectionType>,
    maxHeight: Float, maxWidth: Float,
): Offset {
    return copy(
        x.coerceIn(
            if(blockedDirections.contains(LEFT)) 0f else -maxWidth,
            if(blockedDirections.contains(RIGHT)) 0f else maxWidth
        ), y.coerceIn(
            if(blockedDirections.contains(UP)) 0f else -maxHeight,
            if(blockedDirections.contains(DOWN)) 0f else maxHeight
        )
    )
}

private fun hasNotTravelledEnough(
    state: SwipeableCardState,
    offset: Offset,
): Boolean {
    return abs(offset.x) < state.maxWidth / 4 &&
            abs(offset.y) < state.maxHeight / 4
}
