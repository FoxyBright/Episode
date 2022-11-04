package ru.rikmasters.gilty.mainscreen.presentation.ui.main.custom.swipeablecard

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import kotlin.math.abs


fun Modifier.swipeableCard(
    state: SwipeableCardState,
    onSwiped: (DirectionType) -> Unit,
    onSwipeCancel: () -> Unit = {},
    blockedDirections: List<DirectionType> = listOf(DirectionType.UP, DirectionType.DOWN),
) = pointerInput(Unit) {
    coroutineScope {
        detectDragGestures(
            onDragCancel = {
                launch {
                    state.reset()
                    onSwipeCancel()
                }
            },
            onDrag = { change, dragAmount ->
                launch {
                    val original = state.offset.targetValue
                    val summed = original + dragAmount
                    val newValue = Offset(
                        x = summed.x.coerceIn(-state.maxWidth, state.maxWidth),
                        y = summed.y.coerceIn(-state.maxHeight, state.maxHeight)
                    )
                    if (change.positionChange() != Offset.Zero) change.consume()
                    state.drag(newValue.x, newValue.y)
                }
            },
            onDragEnd = {
                launch {
                    val coercedOffset = state.offset.targetValue
                        .coerceIn(
                            blockedDirections,
                            maxHeight = state.maxHeight,
                            maxWidth = state.maxWidth
                        )

                    if (hasNotTravelledEnough(state, coercedOffset)) {
                        state.reset()
                        onSwipeCancel()
                    } else {
                        val horizontalTravel = abs(state.offset.targetValue.x)
                        val verticalTravel = abs(state.offset.targetValue.y)

                        if (horizontalTravel > verticalTravel) {
                            if (state.offset.targetValue.x > 0) {
                                state.swipe(DirectionType.RIGHT)
                                onSwiped(DirectionType.RIGHT)
                            } else {
                                state.swipe(DirectionType.LEFT)
                                onSwiped(DirectionType.LEFT)
                            }
                        } else {
                            if (state.offset.targetValue.y < 0) {
                                state.swipe(DirectionType.UP)
                                onSwiped(DirectionType.UP)
                            } else {
                                state.swipe(DirectionType.DOWN)
                                onSwiped(DirectionType.DOWN)
                            }
                        }
                    }
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
    maxHeight: Float,
    maxWidth: Float,
): Offset {
    return copy(
        x = x.coerceIn(
            if (blockedDirections.contains(DirectionType.LEFT)) {
                0f
            } else {
                -maxWidth
            },
            if (blockedDirections.contains(DirectionType.RIGHT)) {
                0f
            } else {
                maxWidth
            }
        ),
        y = y.coerceIn(
            if (blockedDirections.contains(DirectionType.UP)) {
                0f
            } else {
                -maxHeight
            },
            if (blockedDirections.contains(DirectionType.DOWN)) {
                0f
            } else {
                maxHeight
            }
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
