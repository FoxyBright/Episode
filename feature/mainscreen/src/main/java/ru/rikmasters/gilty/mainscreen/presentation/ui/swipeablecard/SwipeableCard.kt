package ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard

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
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.LEFT
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.RIGHT
import kotlin.math.abs

fun Modifier.swipeableCard(
    onSwiped: (DirectionType) -> Unit,
    state: SwipeableCardState,
) = pointerInput(Unit) {
    coroutineScope {
        
        suspend fun DirectionType.swipe() {
            state.swipe(this)
            onSwiped(this)
        }
        
        detectDragGestures(
            onDragEnd = {
                launch {
                    val coercedOffset = state
                        .offset
                        .targetValue
                        .let {
                            it.copy(
                                it.x.coerceIn(
                                    -state.maxWidth,
                                    state.maxWidth
                                ), 0f
                            )
                        }
                    
                    if(
                        abs(coercedOffset.x) < state.maxWidth / 4 &&
                        abs(coercedOffset.y) < state.maxHeight / 4
                    ) state.reset()
                    else if(
                        abs(state.offset.targetValue.x) >
                        abs(state.offset.targetValue.y)
                    ) if(state.offset.targetValue.x > 0)
                        RIGHT.swipe() else LEFT.swipe()
                    else state.reset()
                }
            },
            onDrag = { change, dragAmount ->
                launch {
                    val summed = state
                        .offset
                        .targetValue + dragAmount
                    
                    val newValue = Offset(
                        summed.x.coerceIn(
                            -state.maxWidth,
                            state.maxWidth
                        ),
                        summed.y.coerceIn(
                            -state.maxHeight,
                            state.maxHeight
                        )
                    )
                    
                    if(change.positionChange() != Zero)
                        change.consume()
                    
                    state.drag(newValue.x, newValue.y)
                }
            },
            onDragCancel = { launch { state.reset() } }
        )
    }
}.graphicsLayer {
    translationX = state.offset.value.x
    translationY = state.offset.value.y
    rotationZ = (state.offset.value.x / 60)
        .coerceIn(-40f, 40f)
}