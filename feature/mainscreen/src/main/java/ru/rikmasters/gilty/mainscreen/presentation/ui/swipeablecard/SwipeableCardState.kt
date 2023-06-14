package ru.rikmasters.gilty.mainscreen.presentation.ui.swipeablecard

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
import androidx.compose.animation.core.Spring.StiffnessMedium
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.core.util.composable.getConfiguration
import ru.rikmasters.gilty.core.util.composable.getDensity
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType
import ru.rikmasters.gilty.shared.model.enumeration.DirectionType.*

@Composable
fun rememberSwipeableCardState(
    density: Density = getDensity(),
    config: Configuration = getConfiguration(),
) = remember {
    with(density) {
        SwipeableCardState(
            config.screenWidthDp.dp.toPx(),
            config.screenHeightDp.dp.toPx()
        )
    }
}

class SwipeableCardState(
    internal val maxWidth: Float,
    internal val maxHeight: Float,
) {
    
    val offset = Animatable(
        Offset(0f, 0f), Offset.VectorConverter
    )
    
    private val off = offset.value
    
    var swipedDirection: DirectionType? by
    mutableStateOf(null)
    
    internal suspend fun reset() = offset.animateTo(
        Offset(0f, 0f),
        spring(
            dampingRatio = DampingRatioMediumBouncy,
            stiffness = StiffnessMedium
        )
    )
    
    suspend fun swipe(
        direction: DirectionType,
        tween: Int = 500,
    ) {
        offset.animateTo(
            targetValue = when(direction) {
                LEFT -> Offset(-(maxWidth * 1.5f), off.y)
                RIGHT -> Offset((maxWidth * 1.5f), off.y)
                UP -> Offset(off.x, -maxHeight)
                DOWN -> Offset(off.x, maxHeight)
            },
            animationSpec = tween(tween)
        )
        swipedDirection = direction
    }
    
    internal suspend fun drag(
        x: Float, y: Float,
    ) = offset.animateTo(Offset(x, y))
}