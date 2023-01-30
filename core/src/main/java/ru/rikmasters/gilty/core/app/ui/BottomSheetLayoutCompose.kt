package ru.rikmasters.gilty.core.app.ui

import android.content.res.Resources.getSystem
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import ru.rikmasters.gilty.core.app.ui.fork.*
import java.lang.Float.max
import kotlin.math.roundToInt

enum class BottomSheetSwipeState {
    EXPANDED, HALF_EXPANDED, COLLAPSED
}

@Stable
class BottomSheetState(
    internal val swipeableState: SwipeableState<BottomSheetSwipeState>,
) {
    
    internal var content: (@Composable () -> Unit)? by mutableStateOf(null)
    
    suspend fun expand() = animateTo(BottomSheetSwipeState.EXPANDED)
    @Suppress("unused")
    suspend fun halfExpand() = animateTo(BottomSheetSwipeState.HALF_EXPANDED)
    
    suspend fun collapse() {
        animateTo(BottomSheetSwipeState.COLLAPSED)
    }
    
    suspend fun expand(content: @Composable () -> Unit) {
        animateTo(content, BottomSheetSwipeState.EXPANDED)
    }
    
    @Suppress("unused")
    suspend fun halfExpand(content: @Composable () -> Unit) {
        animateTo(content, BottomSheetSwipeState.HALF_EXPANDED)
    }
    
    private suspend fun animateTo(
        content: @Composable () -> Unit,
        swipeState: BottomSheetSwipeState,
    ) {
        animateTo(BottomSheetSwipeState.COLLAPSED)
        this.content = content
        animateTo(swipeState)
    }
    
    private suspend fun animateTo(
        swipeState: BottomSheetSwipeState,
    ) {
        if(swipeState != BottomSheetSwipeState.COLLAPSED && content == null)
            throw IllegalStateException("Открытие нижней панели без содержимого")
        swipeableState.animateTo(swipeState, tween())
    }
    
    val offset: Float by swipeableState.offset
    
    fun ifCollapse(block: () -> Unit) {
        if(offset > getSystem().displayMetrics.heightPixels * 0.99)
            block.invoke()
    }
    
    var gripLightColor: @Composable () -> Color
            by mutableStateOf({ MaterialTheme.colorScheme.outlineVariant })
    var gripDarkColor: @Composable () -> Color
            by mutableStateOf({ MaterialTheme.colorScheme.outline })
    var scrim: @Composable () -> Color
            by mutableStateOf({ MaterialTheme.colorScheme.scrim })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetLayout(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    background: @Composable (@Composable () -> Unit) -> Unit,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier) {
        
        val screenHeight = with(LocalDensity.current) {
            this@BoxWithConstraints.maxHeight.toPx()
        }
        
        var bottomSheetHeight by remember { mutableStateOf<Float?>(null) }
        
        val anchors = remember(bottomSheetHeight, screenHeight) {
            val height = bottomSheetHeight ?: screenHeight
            val anchors = mapOf(
                max(0f, screenHeight - height) to BottomSheetSwipeState.EXPANDED,
                //                screenHeight - height / 2f to BottomSheetSwipeState.HALF_EXPANDED,
                screenHeight to BottomSheetSwipeState.COLLAPSED
            )
            
            state.swipeableState.minBound = anchors.keys.min()
            state.swipeableState.maxBound = anchors.keys.max()
            anchors
        }
        
        val offset = state.swipeableState.offset.value.roundToInt()
        
        val connection = remember { state.swipeableState.PreUpPostDownNestedScrollConnection }
        
        val scope = rememberCoroutineScope()
        
        val scrimColor by animateColorAsState(
            if(state.swipeableState.targetValue == BottomSheetSwipeState.COLLAPSED)
                Color.Transparent else state.scrim().copy(alpha = 0.5f),
            tween(500)
        )
        
        content()
        if(scrimColor != Color.Transparent)
            Box(
                Modifier
                    .fillMaxSize()
                    .background(scrimColor)
                    .clickable { scope.launch { state.collapse() } }
            )
        Box(
            Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, offset) }
                .onGloballyPositioned { coordinates ->
                    coordinates.size.height.let {
                        if(it != 0) bottomSheetHeight = it.toFloat()
                    }
                }
                .swipeable(
                    state.swipeableState,
                    anchors,
                    Orientation.Vertical,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    resistance = null
                )
                .nestedScroll(connection)
        
        ) {
            val focusManager = LocalFocusManager.current
            LaunchedEffect(state.swipeableState.targetValue) {
                focusManager.clearFocus()
            }
            
            LaunchedEffect(state.swipeableState.currentValue) {
                if(state.swipeableState.currentValue == BottomSheetSwipeState.COLLAPSED)
                    state.content = null
            }
            
            val gripColor by animateColorAsState(
                if(state.swipeableState.targetValue == BottomSheetSwipeState.EXPANDED)
                    state.gripDarkColor() else state.gripLightColor()
            )
            val gripOffset by animateDpAsState(
                if(state.swipeableState.targetValue == BottomSheetSwipeState.EXPANDED)
                    21.dp else 0.dp
            )
            Box(
                Modifier
                    .offset(y = 21.dp)
                    .fillMaxWidth()
            ) {
                background(state.content ?: { })
            }
            Grip(
                modifier
                    .offset(y = gripOffset)
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                gripColor
            )
        }
    }
}

@Composable
private fun Grip(
    modifier: Modifier,
    color: Color,
) {
    Box(
        modifier
            .size(40.dp, 5.dp)
            .background(color, RoundedCornerShape(50)),
    )
}