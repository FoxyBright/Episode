package ru.rikmasters.gilty.shared.shared

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.common.extentions.vibrate
import kotlin.math.abs
import kotlin.math.roundToInt

private fun <T> getItemIndexForOffset(
    range: List<T>, value: T, offset: Float,
    halfHeightPx: Float,
) = maxOf(
    (0), minOf(
        a = range.indexOf(value) - (offset / halfHeightPx).toInt(),
        b = range.count() - 1
    )
)

@Composable
fun <T> ListItemPicker(
    value: T, list: List<T>,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    dividersColor: Color = colorScheme.outline,
    textStyle: TextStyle = typography.labelLarge.copy(
        colorScheme.tertiary, textAlign = Center
    ),
    align: Alignment = Alignment.Center,
    horizontalMargin: Dp = 20.dp,
    doublePlaceHolders: Boolean = false,
    onValueChange: (T) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val height = 80.dp
    val verticalMargin = 8.dp
    val halfHeight = height / 2
    val halfHeightPx = with(LocalDensity.current) {
        halfHeight.toPx()
    }
    val animatedOffset = remember {
        Animatable(0f)
    }.apply {
        list.indexOf(value).let { index ->
            remember(value, list) {
                -((list.count() - 1) - index) * halfHeightPx to
                        index * halfHeightPx
            }
        }.let { (lower, upper) ->
            updateBounds(lower, upper)
        }
    }
    val coercedAnimatedOffset = animatedOffset.value % halfHeightPx
    val intOffset = coercedAnimatedOffset.roundToInt()
    val indexOfElement = getItemIndexForOffset(
        list, value, animatedOffset.value, halfHeightPx
    )
    LaunchedEffect(indexOfElement) { vibrate(context) }
    Layout({
        GradientPortal(halfHeight, doublePlaceHolders, (true))
        GDivider(Modifier.fillMaxWidth(), dividersColor)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontalMargin, verticalMargin)
                .offset { IntOffset((0), intOffset) }
        ) {
            val fontWeight = (500 to abs(intOffset))
                .let { (min, max) ->
                    val weight = if(max > 0)
                        minOf(min + max, 600) else min
                    FontWeight(weight)
                }
            val lModifier = Modifier.align(align)
            if(indexOfElement > 1) Label(
                text = label(list.elementAt(indexOfElement - 2)),
                modifier = lModifier.offset(y = -(halfHeight * 2)),
                textStyle = textStyle.copy(fontWeight = W500),
                minAlpha = 0.1f, maxAlpha = 0.1f
            )
            if(indexOfElement > 0) Label(
                text = label(list.elementAt(indexOfElement - 1)),
                modifier = lModifier.offset(y = -halfHeight),
                textStyle = textStyle.copy(fontWeight = fontWeight),
                minAlpha = 0.2f,
                maxAlpha = (coercedAnimatedOffset / (halfHeightPx / 2))
            )
            Label(
                text = label(list.elementAt(indexOfElement)),
                modifier = lModifier,
                textStyle = textStyle.copy(fontWeight = W700),
                minAlpha = 0.3f,
                maxAlpha = (1 - abs(coercedAnimatedOffset) / halfHeightPx)
            )
            if(indexOfElement < list.count() - 1)
                Label(
                    text = label(list.elementAt(indexOfElement + 1)),
                    modifier = lModifier.offset(y = halfHeight),
                    textStyle = textStyle.copy(fontWeight = fontWeight),
                    minAlpha = 0.2f,
                    maxAlpha = (-coercedAnimatedOffset / (halfHeightPx / 2))
                )
            if(indexOfElement < list.count() - 2)
                Label(
                    text = label(list.elementAt(indexOfElement + 2)),
                    modifier = lModifier.offset(y = halfHeight * 2),
                    textStyle = textStyle.copy(fontWeight = W500),
                    minAlpha = 0.1f, maxAlpha = 0.1f
                )
        }
        GDivider(Modifier.fillMaxWidth(), dividersColor)
        GradientPortal(halfHeight, doublePlaceHolders)
    }, modifier
        .draggable(
            rememberDraggableState { deltaY ->
                scope.launch {
                    animatedOffset.snapTo(
                        (animatedOffset.value + deltaY)
                    )
                }
            }, Vertical,
            onDragStopped = {
                onDragStopped(
                    it, halfHeightPx, list, value,
                    animatedOffset, onValueChange
                )
            }
        )
        .padding(0.dp, (height / 3 + verticalMargin * 2))
    ) { measurables, constraints ->
        measurables
            .map { it.measure(constraints) }
            .let { list ->
                layout(
                    list.drop(1).first().width,
                    list.sumOf { it.height }
                ) {
                    var y = 0
                    list.forEach {
                        it.placeRelative((0), y)
                        y += it.height
                    }
                }
            }
    }
}

@Composable
private fun GradientPortal(
    elementHeight: Dp,
    state: Boolean,
    revers: Boolean = false,
    color: Color = colorScheme.background,
) {
    val colors = listOf(color, color, Transparent)
        .let { if(!revers) it.reversed() else it }
    val offset = elementHeight
        .let { if(state) it else it / 3 }
        .let { if(revers) it * -1 else it }
    val height = elementHeight * 3
    Box(
        Modifier
            .zIndex(1f)
            .fillMaxWidth()
            .height(height)
            .offset(0.dp, offset)
            .background(verticalGradient(colors)),
    )
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier,
    textStyle: TextStyle,
    minAlpha: Float = 1f,
    maxAlpha: Float = 1f,
) {
    Text(
        text,
        modifier.alpha(
            maxOf(minAlpha, maxAlpha)
        ),
        style = textStyle,
        maxLines = 1,
    )
}

private suspend fun <T> onDragStopped(
    velocity: Float,
    halfHeightPx: Float,
    list: List<T>, value: T,
    animatedOffset: Animatable<Float, AnimationVector1D>,
    onValueChange: (T) -> Unit,
) {
    val endValue = animatedOffset.fling(
        velocity, exponentialDecay((20f)), { target ->
            (listOf(-halfHeightPx, 0f, halfHeightPx).minByOrNull {
                abs(it - target % halfHeightPx)
            } ?: 0f) + halfHeightPx * (target / halfHeightPx).toInt()
        }
    ).endState.value
    onValueChange(
        list.elementAt(
            getItemIndexForOffset(
                list, value,
                endValue, halfHeightPx
            )
        )
    )
    animatedOffset.snapTo(0f)
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
) = adjustTarget?.invoke(
    animationSpec.calculateTargetValue(value, initialVelocity)
)?.let { animateTo(it, initialVelocity = initialVelocity, block = block) }
    ?: animateDecay(initialVelocity, animationSpec, block)