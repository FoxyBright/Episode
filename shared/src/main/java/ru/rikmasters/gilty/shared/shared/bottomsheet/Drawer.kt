package ru.rikmasters.gilty.shared.shared.bottomsheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

enum class DrawerValue { Closed, Open }
enum class BottomDrawerValue { Closed, Open, Expanded }

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NotCloseable")
@Stable
class DrawerState(
    initialValue: DrawerValue,
    confirmStateChange: (DrawerValue) -> Boolean = { true },
) {
    
    internal val swipeableState = SwipeableState(
        initialValue = initialValue,
        animationSpec = AnimationSpec,
        confirmStateChange = confirmStateChange
    )
    
    val isOpen: Boolean
        get() = currentValue == DrawerValue.Open
    
    @Suppress("unused")
    val isClosed: Boolean
        get() = currentValue == DrawerValue.Closed
    
    val currentValue: DrawerValue
        get() {
            return swipeableState.currentValue
        }
    
    @Suppress("unused")
    val isAnimationRunning: Boolean
        get() {
            return swipeableState.isAnimationRunning
        }
    
    @Suppress("unused")
    suspend fun open() = animateTo(DrawerValue.Open, AnimationSpec)
    
    suspend fun close() = animateTo(DrawerValue.Closed, AnimationSpec)
    
    private suspend fun animateTo(targetValue: DrawerValue, anim: AnimationSpec<Float>) {
        swipeableState.animateTo(targetValue, anim)
    }
    
    @Suppress("unused")
    suspend fun snapTo(targetValue: DrawerValue) {
        swipeableState.snapTo(targetValue)
    }
    
    @Suppress("OPT_IN_MARKER_ON_WRONG_TARGET", "unused")
    val targetValue: DrawerValue
        get() = swipeableState.targetValue
    
    @Suppress("OPT_IN_MARKER_ON_WRONG_TARGET")
    val offset: State<Float>
        get() = swipeableState.offset
    
    companion object {
        
        fun Saver(confirmStateChange: (DrawerValue) -> Boolean) =
            Saver<DrawerState, DrawerValue>(
                save = { it.currentValue },
                restore = { DrawerState(it, confirmStateChange) }
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NotCloseable")
class BottomDrawerState(
    initialValue: BottomDrawerValue,
    confirmStateChange: (BottomDrawerValue) -> Boolean = { true },
): SwipeableState<BottomDrawerValue>(
    initialValue = initialValue,
    animationSpec = AnimationSpec,
    confirmStateChange = confirmStateChange
) {
    
    val isOpen: Boolean
        get() = currentValue != BottomDrawerValue.Closed
    
    @Suppress("unused")
    val isClosed: Boolean
        get() = currentValue == BottomDrawerValue.Closed
    
    @Suppress("unused")
    val isExpanded: Boolean
        get() = currentValue == BottomDrawerValue.Expanded
    
    @Suppress("unused")
    suspend fun open() {
        val targetValue =
            if(isOpenEnabled) BottomDrawerValue.Open else BottomDrawerValue.Expanded
        animateTo(targetValue)
    }
    
    suspend fun close() = animateTo(BottomDrawerValue.Closed)
    suspend fun expand() = animateTo(BottomDrawerValue.Expanded)
    
    private val isOpenEnabled: Boolean
        get() = anchors.values.contains(BottomDrawerValue.Open)
    
    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection
    
    companion object {
        
        fun Saver(confirmStateChange: (BottomDrawerValue) -> Boolean) =
            Saver<BottomDrawerState, BottomDrawerValue>(
                save = { it.currentValue },
                restore = { BottomDrawerState(it, confirmStateChange) }
            )
    }
}

@Composable
fun rememberDrawerState(
    initialValue: DrawerValue,
    confirmStateChange: (DrawerValue) -> Boolean = { true },
): DrawerState {
    return rememberSaveable(saver = DrawerState.Saver(confirmStateChange)) {
        DrawerState(initialValue, confirmStateChange)
    }
}

@Composable
fun rememberBottomDrawerState(
    initialValue: BottomDrawerValue,
    confirmStateChange: (BottomDrawerValue) -> Boolean = { true },
): BottomDrawerState {
    return rememberSaveable(saver = BottomDrawerState.Saver(confirmStateChange)) {
        BottomDrawerState(initialValue, confirmStateChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDrawer(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    @Suppress("unused_parameter")
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val modalDrawerConstraints = constraints
        // TODO : think about Infinite max bounds case
        if(!modalDrawerConstraints.hasBoundedWidth) {
            throw IllegalStateException("Drawer shouldn't have infinite width")
        }
        
        val minValue = -modalDrawerConstraints.maxWidth.toFloat()
        val maxValue = 0f
        
        val anchors = mapOf(minValue to DrawerValue.Closed, maxValue to DrawerValue.Open)
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        Box(
            Modifier.swipeable(
                state = drawerState.swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled,
                reverseDirection = isRtl,
                velocityThreshold = DrawerVelocityThreshold,
                resistance = null
            )
        ) {
            Box {
                content()
            }
            Scrim(
                open = drawerState.isOpen,
                onClose = {
                    if(
                        gesturesEnabled &&
                        drawerState.swipeableState.confirmStateChange(DrawerValue.Closed)
                    ) {
                        scope.launch { drawerState.close() }
                    }
                },
                fraction = {
                    calculateFraction(minValue, maxValue, drawerState.offset.value)
                },
                color = scrimColor
            )
            val navigationMenu = getString(Strings.NavigationMenu)
            Surface(
                modifier = with(LocalDensity.current) {
                    Modifier
                        .sizeIn(
                            minWidth = modalDrawerConstraints.minWidth.toDp(),
                            minHeight = modalDrawerConstraints.minHeight.toDp(),
                            maxWidth = modalDrawerConstraints.maxWidth.toDp(),
                            maxHeight = modalDrawerConstraints.maxHeight.toDp()
                        )
                }
                    .offset { IntOffset(drawerState.offset.value.roundToInt(), 0) }
                    .padding(end = EndDrawerPadding)
                    .semantics {
                        paneTitle = navigationMenu
                        if(drawerState.isOpen) {
                            dismiss {
                                if(
                                    drawerState.swipeableState
                                        .confirmStateChange(DrawerValue.Closed)
                                ) {
                                    scope.launch { drawerState.close() }
                                }; true
                            }
                        }
                    },
                shape = drawerShape,
                color = drawerBackgroundColor,
                contentColor = drawerContentColor,
                // does not exist in material3
                // elevation = drawerElevation
            ) {
                Column(Modifier.fillMaxSize(), content = drawerContent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("unused")
fun BottomDrawer(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    @Suppress("unused_parameter")
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    
    BoxWithConstraints(modifier.fillMaxSize()) {
        val fullHeight = constraints.maxHeight.toFloat()
        var drawerHeight by remember(fullHeight) { mutableStateOf(fullHeight) }
        // TODO(b/178630869) Proper landscape support
        val isLandscape = constraints.maxWidth > constraints.maxHeight
        
        val minHeight = 0f
        val peekHeight = fullHeight * BottomDrawerOpenFraction
        val expandedHeight = max(minHeight, fullHeight - drawerHeight)
        val anchors = if(drawerHeight < peekHeight || isLandscape) {
            mapOf(
                fullHeight to BottomDrawerValue.Closed,
                expandedHeight to BottomDrawerValue.Expanded
            )
        } else {
            mapOf(
                fullHeight to BottomDrawerValue.Closed,
                peekHeight to BottomDrawerValue.Open,
                expandedHeight to BottomDrawerValue.Expanded
            )
        }
        val drawerConstraints = with(LocalDensity.current) {
            Modifier
                .sizeIn(
                    maxWidth = constraints.maxWidth.toDp(),
                    maxHeight = constraints.maxHeight.toDp()
                )
        }
        val nestedScroll = if(gesturesEnabled) {
            Modifier.nestedScroll(drawerState.nestedScrollConnection)
        } else {
            Modifier
        }
        val swipeable = Modifier
            .then(nestedScroll)
            .swipeable(
                state = drawerState,
                anchors = anchors,
                orientation = Orientation.Vertical,
                enabled = gesturesEnabled,
                resistance = null
            )
        
        Box(swipeable) {
            content()
            BottomDrawerScrim(
                color = scrimColor,
                onDismiss = {
                    if(
                        gesturesEnabled && drawerState.confirmStateChange(BottomDrawerValue.Closed)
                    ) {
                        scope.launch { drawerState.close() }
                    }
                },
                visible = drawerState.targetValue != BottomDrawerValue.Closed
            )
            val navigationMenu = getString(Strings.NavigationMenu)
            Surface(
                drawerConstraints
                    .offset { IntOffset(x = 0, y = drawerState.offset.value.roundToInt()) }
                    .onGloballyPositioned { position ->
                        drawerHeight = position.size.height.toFloat()
                    }
                    .semantics {
                        paneTitle = navigationMenu
                        if(drawerState.isOpen) {
                            // TODO(b/180101663) The action currently doesn't return the correct results
                            dismiss {
                                if(drawerState.confirmStateChange(BottomDrawerValue.Closed)) {
                                    scope.launch { drawerState.close() }
                                }; true
                            }
                        }
                    },
                shape = drawerShape,
                color = drawerBackgroundColor,
                contentColor = drawerContentColor
                // parameter does not exist in material3
                // elevation = drawerElevation
            ) {
                Column(content = drawerContent)
            }
        }
    }
}

object DrawerDefaults {
    
    val Elevation = 16.dp
    
    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface.copy(alpha = ScrimOpacity)
    
    private const val ScrimOpacity = 0.32f
}

private fun calculateFraction(
    a: Float,
    @Suppress("SameParameterValue")
    b: Float,
    pos: Float,
) = ((pos - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun BottomDrawerScrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean,
) {
    if(color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if(visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val closeDrawer = getString(Strings.CloseDrawer)
        val dismissModifier = if(visible) {
            Modifier
                .pointerInput(onDismiss) {
                    detectTapGestures { onDismiss() }
                }
                .semantics(mergeDescendants = true) {
                    contentDescription = closeDrawer
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }
        
        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color,
) {
    val closeDrawer = getString(Strings.CloseDrawer)
    val dismissDrawer = if(open) {
        Modifier
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics(mergeDescendants = true) {
                contentDescription = closeDrawer
                onClick { onClose(); true }
            }
    } else {
        Modifier
    }
    
    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

private val AnimationSpec = TweenSpec<Float>(durationMillis = 256)
private const val BottomDrawerOpenFraction = 0.5f
private val DrawerVelocityThreshold = 400.dp
private val EndDrawerPadding = 56.dp
