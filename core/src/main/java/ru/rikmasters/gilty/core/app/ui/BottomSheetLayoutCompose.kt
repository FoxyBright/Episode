package ru.rikmasters.gilty.core.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.EXPANDED
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.HALF_EXPANDED
import ru.rikmasters.gilty.core.app.ui.fork.*
import java.lang.Float.max
import kotlin.math.roundToInt

enum class BottomSheetSwipeState { EXPANDED, HALF_EXPANDED, COLLAPSED }

@Stable
class BottomSheetState(
    internal val swipeableState: SwipeableState<BottomSheetSwipeState>,
) {
    
    internal var content: (@Composable () -> Unit)? by mutableStateOf(null)
    suspend fun expand() = animateTo(EXPANDED)
    
    @Suppress("unused")
    suspend fun halfExpand() =
        animateTo(HALF_EXPANDED)
    
    suspend fun collapse() {
        animateTo(COLLAPSED)
    }
    
    suspend fun expand(content: @Composable () -> Unit) {
        animateTo(content, EXPANDED)
    }
    
    @Suppress("unused")
    suspend fun halfExpand(content: @Composable () -> Unit) {
        animateTo(content, HALF_EXPANDED)
    }
    
    private suspend fun animateTo(
        content: @Composable () -> Unit,
        swipeState: BottomSheetSwipeState,
    ) {
        animateTo(COLLAPSED)
        this.content = content
        
        animateTo(swipeState)
    }
    
    private suspend fun animateTo(
        swipeState: BottomSheetSwipeState,
    ) {
        if(swipeState != COLLAPSED && content == null)
            throw IllegalStateException("Открытие нижней панели без содержимого")
        swipeableState.animateTo(swipeState, tween())
    }
    
    @Suppress("PropertyName")
    internal val _current =
        MutableStateFlow(COLLAPSED)
    val current = _current.asStateFlow()
    
    val colors: @Composable () -> Color = {
        if(isSystemInDarkTheme())
            Color(0xFF767373)
        else Color(0xFFC9C5CA)
    }
    
    var gripLightColor: @Composable () -> Color
            by mutableStateOf(colors)
    var gripDarkColor: @Composable () -> Color
            by mutableStateOf(colors)
    var scrim: @Composable () -> Color
            by mutableStateOf({ colorScheme.scrim })
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
                max(
                    0f,
                    screenHeight - height
                ) to EXPANDED,
                // screenHeight - height / 2f to HALF_EXPANDED,
                screenHeight to COLLAPSED
            )
            
            state.swipeableState.minBound = anchors.keys.min()
            state.swipeableState.maxBound = anchors.keys.max()
            anchors
        }
        
        val offset = state.swipeableState.offset.value.roundToInt()
        
        val connection =
            remember { state.swipeableState.PreUpPostDownNestedScrollConnection }
        
        val scope = rememberCoroutineScope()
        
        val scrimColor by animateColorAsState(
            if(state.swipeableState.targetValue == COLLAPSED)
                Color.Transparent else state.scrim().copy(alpha = 0.5f),
            tween(500), label = ""
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
                state._current.emit(state.swipeableState.currentValue)
                if(state.swipeableState.currentValue == COLLAPSED)
                    state.content = null
            }
            
            val gripColor by animateColorAsState(
                if(state.swipeableState.targetValue == EXPANDED)
                    state.gripDarkColor() else state.gripLightColor(),
                label = ""
            )
            val gripOffset by animateDpAsState(
                if(state.swipeableState.targetValue == EXPANDED)
                    21.dp else 0.dp, label = ""
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
private fun Grip(modifier: Modifier, color: Color) {
    Box(
        modifier
            .size(40.dp, 5.dp)
            .background(color, RoundedCornerShape(50)),
    )
}