package ru.rikmasters.gilty.translation.shared.presentation.ui.content.bottomsheet

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.vibrate
import ru.rikmasters.gilty.shared.shared.GDivider
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ExtendBottomSheet(
    onSave: ((Int) -> Unit),
    configuration: Configuration
) {
    var current by remember { mutableStateOf("") }
    DurationBottomSheet(
        value = current,
        modifier = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(
                        topStart = 24.dp
                    )
                )
                .padding(horizontal = 16.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(
                    color = Color.Transparent
                )
                .padding(horizontal = 16.dp)
        },
        online = true,
        { current = it },
    ) {
        if (current.isNotBlank()) {
            if (current.contains("1")) {
                if (current.trim().last() == 'с') {
                    onSave(60)
                } else {
                    val minutes = current.substringAfterLast('с').substringBeforeLast('м').trim().toInt()
                    onSave(minutes + 60)
                }
            } else {
                val minutes = current.substringBeforeLast('м').trim().toInt()
                onSave(minutes)
            }
        } else {
            onSave(60)
        }
    }
}





@Composable
fun DurationBottomSheet(
    value: String,
    modifier: Modifier = Modifier,
    online: Boolean,
    onValueChange: ((String) -> Unit)? = null,
    onSave: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .background(color = Color.Black)
    ) {
        DurationPicker(
            value, Modifier
                .wrapContentSize()
                .background(Color.Black)
                .align(Alignment.Center)
        ) { onValueChange?.let { c -> c(it) } }
        Column(
            modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .height(5.dp)
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(11.dp),
                color = ThemeExtra.colors.bottomSheetGray
            ) {}
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                stringResource(R.string.translations_expired_append),
                Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Start),
                ThemeExtra.colors.white,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            GradientButton(
                Modifier
                    .padding(vertical = 28.dp)
                    .align(Alignment.CenterHorizontally),
                stringResource(R.string.translations_expired_button_continue),
                online = online
            ) { onSave?.let { it() } }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DurationPicker(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> Unit)? = null,
) {
    Box(
        modifier.background(
            Color.Black
        ), Alignment.Center
    ) {
        Box(
            Modifier.padding(horizontal = 20.dp)
                .background(Color.Black)
        ) {
            getDuration().let { list ->
                ListItemPicker(
                    modifier = Modifier.background(
                        color = Color.Black
                    ),
                    value = value.ifBlank {
                        list[list.lastIndex - 2]
                    },
                    list = list,
                    doublePlaceHolders = true,
                    dividersColor = Color(0xFF767373),
                    textStyle = MaterialTheme.typography.labelLarge.copy(
                        ThemeExtra.colors.white, textAlign = TextAlign.Center
                    )
                ) { onValueChange?.let { c -> c(it) } }
            }
        }
    }
}

fun getDuration() = arrayListOf<String>().let { list ->
    var hour = 0
    var min = 30
    repeat(5) {
        val hLabel = "$hour ${if(hour == 1) "час" else "часа"}"
        val mLabel = if(min > 0) "$min минут" else ""
        list.add("${if(hour > 0) "$hLabel " else ""}$mLabel")
        min += 15
        if(min == 60) {
            min = 0; ++hour
        }
    }; list.reverse()
    list
}

@Composable
fun <T> ListItemPicker(
    value: T, list: List<T>,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    dividersColor: Color = MaterialTheme.colorScheme.outline,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(
        MaterialTheme.colorScheme.tertiary, textAlign = TextAlign.Center
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
                textStyle = textStyle.copy(fontWeight = FontWeight.W500),
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
                textStyle = textStyle.copy(fontWeight = FontWeight.W700),
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
                    textStyle = textStyle.copy(fontWeight = FontWeight.W500),
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
            }, Orientation.Vertical,
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
    color: Color = Color.Black,
) {
    val colors = listOf(color, color, Color.Transparent)
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
            .background(Brush.verticalGradient(colors)),
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

private fun <T> getItemIndexForOffset(
    range: List<T>, value: T, offset: Float,
    halfHeightPx: Float,
) = maxOf(
    (0), minOf(
        a = range.indexOf(value) - (offset / halfHeightPx).toInt(),
        b = range.count() - 1
    )
)