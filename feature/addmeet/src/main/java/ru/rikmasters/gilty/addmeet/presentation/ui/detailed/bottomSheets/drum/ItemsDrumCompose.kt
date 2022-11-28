package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottomSheets.drum

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.common.extentions.getDate
import ru.rikmasters.gilty.shared.common.extentions.getTime
import ru.rikmasters.gilty.shared.common.extentions.replacer
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import kotlin.math.abs
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
private fun DrumPreview() {
    var date by remember { mutableStateOf("Сегодня") }
    var hour by remember { mutableStateOf("00") }
    var minute by remember { mutableStateOf("00") }
    GiltyTheme {
        Row(
            Modifier
                .fillMaxWidth()
                .background(colorScheme.background),
            Arrangement.Center, Alignment.CenterVertically
        ) {
            ItemsDrum(date, getDate()) { date = it }
            ItemsDrum(
                replacer(hour, "24"),
                getTime(0..24, 1),
                Modifier, { replacer(it, "24") }
            ) { hour = it }
            ItemsDrum(
                replacer(minute, "60"),
                getTime(0..60, 5),
                Modifier, { replacer(it, "60") }
            ) { minute = it }
        }
    }
}

@Composable
fun <T> ItemsDrum(
    value: T, list: List<T>,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    onValueChange: (T) -> Unit,
) {
    val halfHeightPx = with(LocalDensity.current) { 80.dp.toPx() }
    val scope = rememberCoroutineScope()
    val offset = remember { Animatable(0f) }
        .apply {
            val index = list.indexOf(value)
            val offsetRange = remember(value, list) {
                -((list.count() - 1) - index) * halfHeightPx to
                        index * halfHeightPx
            }; updateBounds(offsetRange.first, offsetRange.second)
        }
    val coercedOffset = offset.value % halfHeightPx
    val indexOfElement =
        getItemIndexForOffset(list, value, offset.value, halfHeightPx)
    var liseWidth by remember { mutableStateOf(0.dp) }
    Layout(
        {
            Line(liseWidth); Box(
            Modifier
                .padding(20.dp, 8.dp)
                .offset { IntOffset((0), coercedOffset.roundToInt()) }
        ) {
            val labelModifier = Modifier.align(Alignment.Center)
            ProvideTextStyle(typography.bodyMedium) {
                if (indexOfElement > 0)
                    Label(
                        label(list.elementAt(indexOfElement - 1)),
                        labelModifier
                            .offset((0.dp), -(40).dp)
                            .alpha(maxOf(0.3f, coercedOffset*2 / halfHeightPx))
                    )
                Label(
                    label(list.elementAt(indexOfElement)),
                    labelModifier
                        .alpha((maxOf(0f, 1 - abs(coercedOffset) / halfHeightPx)))
                )
                if (indexOfElement < list.count() - 1)
                    Label(
                        label(list.elementAt(indexOfElement + 1)),
                        labelModifier
                            .offset(0.dp, 40.dp)
                            .alpha(maxOf(0.3f, -coercedOffset*2 / halfHeightPx))
                    )
            }
        }; Line(liseWidth)
        }, modifier
            .draggable(
                rememberDraggableState {
                    scope.launch {
                        offset.snapTo(offset.value + it)
                    }
                }, Vertical, onDragStopped = {
                    scope.launch {
                        onValueChange(
                            list.elementAt(
                                getItemIndexForOffset(
                                    list, value, offset.fling(
                                        it, exponentialDecay((20f))
                                    ) { target ->
                                        listOf(-halfHeightPx, 0f, halfHeightPx).minByOrNull {
                                            abs(it - target % halfHeightPx)
                                        }!! + halfHeightPx * (target / halfHeightPx).toInt()
                                    }.endState.value, halfHeightPx
                                )
                            )
                        )
                        offset.snapTo((0f))
                    }
                }
            )
            .padding(0.dp, 82.dp)
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        liseWidth = placeables.drop(1).first().width.toDp()
        layout(liseWidth.toPx().toInt(), placeables.sumOf { it.height }
        ) {
            var yPosition = 0; placeables.forEach { placeable ->
            placeable.placeRelative((0), yPosition)
            yPosition += placeable.height
        }
        }
    }
}

@Composable
private fun Label(text: String, modifier: Modifier, weight: FontWeight = Bold) {
    Text(
        text, modifier.pointerInput(Unit) { detectTapGestures() },
        textAlign = Center, fontWeight = weight
    )
}

@Composable
private fun Line(width: Dp) {
    Divider(Modifier.width(width), 2.dp, colorScheme.outline)
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
): AnimationResult<Float, AnimationVector1D> {
    adjustTarget?.invoke(animationSpec.calculateTargetValue(value, initialVelocity))?.let {
        return animateTo(it, block = null, initialVelocity = initialVelocity)
    }; return animateDecay(initialVelocity, animationSpec, (null))
}

private fun <T> getItemIndexForOffset(
    range: List<T>, value: T,
    offset: Float, height: Float
): Int {
    val indexOf = range.indexOf(value) - (offset / height).toInt()
    return maxOf(0, minOf(indexOf, range.count() - 1))
}