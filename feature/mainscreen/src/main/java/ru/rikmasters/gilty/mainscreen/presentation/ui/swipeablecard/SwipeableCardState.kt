package ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType

@Composable
fun rememberSwipeableCardState(): SwipeableCardState {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val screenHeight = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    return remember { SwipeableCardState(screenWidth, screenHeight) }
}

class SwipeableCardState(
    internal val maxWidth: Float,
    internal val maxHeight: Float,
) {
    val offset = Animatable(offset(0f, 0f), Offset.VectorConverter)
    var swipedDirection: DirectionType? by mutableStateOf(null)
    internal suspend fun reset() {
        offset.animateTo(offset(0f, 0f), tween(1000))
    }

    suspend fun swipe(
        direction: DirectionType,
        animationSpec: AnimationSpec<Offset> =
            tween(1000)
    ) {
        when (direction) {
            DirectionType.LEFT -> offset.animateTo(offset(x = -(maxWidth * 1.5f)), animationSpec)
            DirectionType.RIGHT -> offset.animateTo(offset(x = (maxWidth * 1.5f)), animationSpec)
            DirectionType.UP -> offset.animateTo(offset(y = -maxHeight), animationSpec)
            DirectionType.DOWN -> offset.animateTo(offset(y = maxHeight), animationSpec)
        }; this.swipedDirection = direction
    }

    private fun offset(x: Float = offset.value.x, y: Float = offset.value.y): Offset {
        return Offset(x, y)
    }

    internal suspend fun drag(x: Float, y: Float) {
        offset.animateTo(offset(x, y))
    }
}
