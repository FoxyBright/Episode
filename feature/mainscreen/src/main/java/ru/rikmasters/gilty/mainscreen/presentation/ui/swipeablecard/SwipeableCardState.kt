package ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.*

@Composable
fun rememberSwipeableCardState(): SwipeableCardState {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    return remember {
        with(density) {
            SwipeableCardState(
                config.screenWidthDp.dp.toPx(),
                config.screenHeightDp.dp.toPx()
            )
        }
    }
}

class SwipeableCardState(
    internal val maxWidth: Float,
    internal val maxHeight: Float,
) {
    
    private val defaultTween = 500
    
    val offset = Animatable(
        Offset(0f, 0f), Offset.VectorConverter
    )
    
    private val off = offset.value
    
    var swipedDirection: DirectionType? by
    mutableStateOf(null)
    
    internal suspend fun reset(
        tween: Int = defaultTween,
    ) = offset.animateTo(
        Offset(0f, 0f),
        tween(tween)
    )
    
    suspend fun swipe(
        direction: DirectionType,
        tween: Int = defaultTween,
    ) {
        offset.animateTo(
            when(direction) {
                LEFT -> Offset(-(maxWidth * 1.5f), off.y)
                RIGHT -> Offset((maxWidth * 1.5f), off.y)
                UP -> Offset(off.x, -maxHeight)
                DOWN -> Offset(off.x, maxHeight)
            }, tween(tween)
        )
        swipedDirection = direction
    }
    
    internal suspend fun drag(
        x: Float, y: Float,
    ) = offset.animateTo(Offset(x, y))
}