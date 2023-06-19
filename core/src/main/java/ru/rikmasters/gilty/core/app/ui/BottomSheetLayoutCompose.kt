package ru.rikmasters.gilty.core.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.EXPANDED
import ru.rikmasters.gilty.core.app.ui.fork.*
import ru.rikmasters.gilty.core.util.composable.getDensity
import java.lang.Float.max
import kotlin.math.roundToInt

enum class BottomSheetSwipeState { EXPANDED, COLLAPSED }

@Stable
class BottomSheetState(
    val swipeableState: SwipeableState<BottomSheetSwipeState>,
) {
    
    internal var content: (@Composable () -> Unit)?
            by mutableStateOf(null)
    
    suspend fun expand() = animateTo(EXPANDED)
    
    suspend fun collapse() {
        animateTo(COLLAPSED)
    }
    
    suspend fun expand(content: @Composable () -> Unit) {
        animateTo(content, EXPANDED)
    }
    
    private suspend fun animateTo(
        content: @Composable () -> Unit,
        swipeState: BottomSheetSwipeState,
    ) {
        this.content = content
        animateTo(swipeState)
    }
    
    private suspend fun animateTo(
        swipeState: BottomSheetSwipeState,
    ) {
        if(swipeState != COLLAPSED && content == null)
            throw IllegalStateException(
                "Открытие нижней панели без содержимого"
            )
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
    background: @Composable ((@Composable () -> Unit)?) -> Unit,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier) {
        
        val screenHeight = with(getDensity()) {
            this@BoxWithConstraints.maxHeight.toPx()
        }
        
        var bottomSheetHeight by remember {
            mutableStateOf<Float?>(null)
        }
        
        val swipeState =
            remember(state.swipeableState) { state.swipeableState }
        
        val anchors = remember(
            bottomSheetHeight, screenHeight
        ) {
            val height = bottomSheetHeight ?: screenHeight
            val anchors = mapOf(
                max(
                    0f,
                    screenHeight - height
                ) to EXPANDED,
                screenHeight to COLLAPSED
            )
            
            swipeState.minBound = anchors.keys.min()
            swipeState.maxBound = anchors.keys.max()
            anchors
        }
        
        val offset = swipeState.offset.value.roundToInt()
        
        val screenName = swipeState.currentScreenName.value
        
        val connection = remember {
            swipeState.PreUpPostDownNestedScrollConnection
        }
        
        val scope = rememberCoroutineScope()
        
        val scrimColor by animateColorAsState(
            if(swipeState.targetValue == COLLAPSED)
                Transparent else colorScheme.background
                .copy(alpha = 0.5f),
            tween(500),
            label = ""
        )
        
        content()
        
        if(scrimColor != Transparent)
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
                    state = swipeState,
                    anchors = anchors,
                    orientation = Vertical,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    resistance = null,
                    enabled = screenName != "Map"
                )
                .nestedScroll(connection)
        
        ) {
            val focusManager = LocalFocusManager.current
            LaunchedEffect(swipeState.targetValue) {
                focusManager.clearFocus()
            }
            
            LaunchedEffect(swipeState.currentValue) {
                state._current.emit(swipeState.currentValue)
                if(swipeState.currentValue == COLLAPSED)
                    state.content = null
            }
            
            val gripColor by animateColorAsState(
                if(swipeState.targetValue == EXPANDED)
                    state.gripDarkColor()
                else state.gripLightColor(),
                label = ""
            )
            val gripOffset by animateDpAsState(
                if(swipeState.targetValue == EXPANDED)
                    21.dp else 0.dp, label = ""
            )
            state.content?.let {
                Box(
                    Modifier
                        .offset(y = 21.dp)
                        .fillMaxWidth()
                ) { background(it) }
                Grip(
                    modifier = modifier
                        .offset(y = gripOffset)
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                    color = gripColor
                )
            }
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
            .background(
                color = color,
                shape = CircleShape
            ),
    )
}