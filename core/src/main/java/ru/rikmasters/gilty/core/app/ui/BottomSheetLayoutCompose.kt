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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.app.AppStateModel
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
    suspend fun collapse() = animateTo(COLLAPSED)

    suspend fun expand(content: @Composable () -> Unit) =
        animateTo(content, EXPANDED)

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
    val current =
        _current.asStateFlow()

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
    asm: AppStateModel,
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    background: @Composable ((@Composable () -> Unit)?) -> Unit,
    content: @Composable () -> Unit,
) {
    val swipeState = state.getSwipeState()
    val isCollapsed = swipeState.getCollapsed()
    val statusColor by getStatusBarColor(isCollapsed)
    val navColor = colorScheme.primaryContainer
    val scope = rememberCoroutineScope()
    var bottomSheetHeight by rememberSaveable {
        mutableStateOf<Float?>(null)
    }
    val density = getDensity()

    val offset = swipeState.offset.value.roundToInt()

    val enabled = remember(swipeState.currentScreenName.value) {
        swipeState.currentScreenName.value != "Map"
    }

    val connection = remember {
        swipeState.PreUpPostDownNestedScrollConnection
    }

    BoxWithConstraints(modifier) {
        LaunchedEffect(swipeState.targetValue) {
            asm.systemUi.setSystemBarsColor(statusColor)
            asm.systemUi.setNavigationBarColor(navColor)
        }
        content()

        Scrim(
            isDark = isSystemInDarkTheme(),
            state = state,
            collapsed = isCollapsed,
            scope = scope
        )
        BottomSheet(
            connection = connection,
            swipeState = swipeState,
            enabled = enabled,
            offset = offset,
            state = state,
            anchors = swipeState.getAnchors(
                bottomSheetHeight = bottomSheetHeight,
                screenHeight = rememberSaveable {
                    maxHeight.toPx(density)
                }
            ),
            onGloballyPositioned = { coordinates ->
                coordinates.size.height.let {
                    if(it != 0) bottomSheetHeight =
                        it.toFloat()
                    else scope.launch {
                        state.collapse()
                    }
                }
            }
        ) { background(it) }
    }
}

@Composable
private fun getStatusBarColor(
    isCollapsed: Boolean,
) = animateColorAsState(
    targetValue = when {
        isSystemInDarkTheme() -> Black
        isCollapsed -> Color(0xFFB8B8B8)
        else -> colorScheme.background
    },
    animationSpec = tween(500),
    label = ""
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeableState<BottomSheetSwipeState>.getCollapsed() =
    rememberSaveable(targetValue) { targetValue == COLLAPSED }

@Composable
private fun BottomSheetState.getSwipeState() =
    remember(swipeableState) { swipeableState }

@Composable
private fun Scrim(
    isDark: Boolean,
    state: BottomSheetState,
    collapsed: Boolean,
    scope: CoroutineScope,
    backColor: Color = remember(isDark) {
        Black.copy(if(isDark) 0.45f else 0.25f)
    },
) {
    animateColorAsState(
        targetValue = if(collapsed)
            Transparent else backColor,
        animationSpec = tween(500),
        label = ""
    ).value.let { scrimColor ->
        if(scrimColor != Transparent) Box(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .navigationBarsPadding()
                .background(scrimColor)
                .clickable {
                    scope.launch {
                        state.collapse()
                    }
                }
        )
    }
}


@Composable
private fun SwipeableState<BottomSheetSwipeState>.getAnchors(
    bottomSheetHeight: Float?,
    screenHeight: Float,
) = rememberSaveable(
    bottomSheetHeight, screenHeight
) {
    val height = bottomSheetHeight ?: screenHeight
    val anchors = mapOf(
        max(0f, screenHeight - height) to EXPANDED,
        screenHeight to COLLAPSED
    )
    minBound = anchors.keys.min()
    maxBound = anchors.keys.max()
    anchors
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheet(
    offset: Int,
    enabled: Boolean,
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    connection: NestedScrollConnection,
    anchors: Map<Float, BottomSheetSwipeState>,
    swipeState: SwipeableState<BottomSheetSwipeState>,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
    background: @Composable (
        @Composable (() -> Unit)?,
    ) -> Unit,
) {
    Box(
        modifier
            .fillMaxWidth()
            .offset { IntOffset(0, offset) }
            .onGloballyPositioned {
                onGloballyPositioned(it)
            }
            .swipeable(
                state = swipeState,
                anchors = anchors,
                orientation = Vertical,
                thresholds = { _, _ ->
                    FractionalThreshold(0.3f)
                },
                resistance = null,
                enabled = enabled
            )
            .nestedScroll(connection)
    ) {
        BottomSheetContent(
            state = state,
            swipeState = swipeState,
            modifier = Modifier
                .align(TopCenter)
        ) { background(it) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheetContent(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    swipeState: SwipeableState<BottomSheetSwipeState>,
    background: @Composable (
        @Composable (() -> Unit)?,
    ) -> Unit,
) {
    val focusManager = LocalFocusManager.current
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
    LaunchedEffect(swipeState.targetValue) {
        focusManager.clearFocus()
    }
    LaunchedEffect(swipeState.currentValue) {
        state._current.emit(swipeState.currentValue)
        if(swipeState.currentValue == COLLAPSED)
            state.content = null
    }
    state.content?.let {
        Box(
            Modifier
                .offset(y = 21.dp)
                .fillMaxWidth()
                .systemBarsPadding()
                .navigationBarsPadding()
        ) { background(it) }
        Grip(
            modifier = modifier
                .offset(y = gripOffset)
                .padding(top = 8.dp)
                .systemBarsPadding()
                .navigationBarsPadding(),
            color = gripColor
        )
    }
}

private fun Dp.toPx(
    density: Density,
) = with(density) {
    this@toPx.toPx()
}

@Composable
private fun Grip(
    modifier: Modifier,
    color: Color,
) = Box(
    modifier
        .size(40.dp, 5.dp)
        .background(
            color = color,
            shape = CircleShape
        ),
)