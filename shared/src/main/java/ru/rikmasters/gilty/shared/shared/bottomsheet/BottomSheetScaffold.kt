package ru.rikmasters.gilty.shared.shared.bottomsheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class BottomSheetValue { Collapsed, Expanded }

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class BottomSheetState(
    initialValue: BottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true },
): SwipeableState<BottomSheetValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    
    @Suppress("unused")
    val isExpanded: Boolean
        get() = currentValue == BottomSheetValue.Expanded
    
    val isCollapsed: Boolean
        get() = currentValue == BottomSheetValue.Collapsed
    
    suspend fun expand() = animateTo(BottomSheetValue.Expanded)
    
    suspend fun collapse() = animateTo(BottomSheetValue.Collapsed)
    
    companion object {
        
        fun saver(
            animationSpec: AnimationSpec<Float>,
            confirmStateChange: (BottomSheetValue) -> Boolean,
        ): Saver<BottomSheetState, *> = Saver(
            save = { it.currentValue },
            restore = {
                BottomSheetState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    confirmStateChange = confirmStateChange
                )
            }
        )
    }
    
    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection
}

@Composable
fun rememberBottomSheetState(
    initialValue: BottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true },
): BottomSheetState {
    return rememberSaveable(
        animationSpec,
        saver = BottomSheetState.saver(
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    ) {
        BottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    }
}

@Stable
class BottomSheetScaffoldState(
    val drawerState: DrawerState,
    val bottomSheetState: BottomSheetState,
    val snackbarHostState: SnackbarHostState,
)

@Composable
fun rememberBottomSheetScaffoldState(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    bottomSheetState: BottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): BottomSheetScaffoldState {
    return remember(drawerState, bottomSheetState, snackbarHostState) {
        BottomSheetScaffoldState(
            drawerState = drawerState,
            bottomSheetState = bottomSheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("unused")
fun BottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    topBar: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: (@Composable () -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    sheetGesturesEnabled: Boolean = true,
    sheetShape: Shape = MaterialTheme.shapes.large,
    @Suppress("unused_parameter")
    sheetElevation: Dp = BottomSheetScaffoldDefaults.SheetElevation,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPeekHeight: Dp = BottomSheetScaffoldDefaults.SheetPeekHeight,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    drawerScrimColor: Color = DrawerDefaults.scrimColor,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (PaddingValues) -> Unit,
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(LocalDensity.current) { sheetPeekHeight.toPx() }
        var bottomSheetHeight by remember { mutableStateOf(fullHeight) }
        
        val swipeable = Modifier
            .nestedScroll(scaffoldState.bottomSheetState.nestedScrollConnection)
            .swipeable(
                state = scaffoldState.bottomSheetState,
                anchors = mapOf(
                    fullHeight - peekHeightPx to BottomSheetValue.Collapsed,
                    fullHeight - bottomSheetHeight to BottomSheetValue.Expanded
                ),
                orientation = Orientation.Vertical,
                enabled = sheetGesturesEnabled,
                resistance = null
            )
            .semantics {
                if(peekHeightPx != bottomSheetHeight) {
                    if(scaffoldState.bottomSheetState.isCollapsed) {
                        expand {
                            if(scaffoldState.bottomSheetState.confirmStateChange(BottomSheetValue.Expanded)) {
                                scope.launch { scaffoldState.bottomSheetState.expand() }
                            }
                            true
                        }
                    } else {
                        collapse {
                            if(scaffoldState.bottomSheetState.confirmStateChange(BottomSheetValue.Collapsed)) {
                                scope.launch { scaffoldState.bottomSheetState.collapse() }
                            }
                            true
                        }
                    }
                }
            }
        
        val child = @Composable {
            BottomSheetScaffoldStack(
                body = {
                    Surface(
                        color = backgroundColor,
                        contentColor = contentColor
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            topBar?.invoke()
                            content(PaddingValues(bottom = sheetPeekHeight))
                        }
                    }
                },
                bottomSheet = {
                    Surface(
                        swipeable
                            .fillMaxWidth()
                            .requiredHeightIn(min = sheetPeekHeight)
                            .onGloballyPositioned {
                                bottomSheetHeight = it.size.height.toFloat()
                            },
                        shape = sheetShape,
                        // parameter does not exists in material3
                        // elevation = sheetElevation,
                        color = sheetBackgroundColor,
                        contentColor = sheetContentColor,
                        content = { Column(content = sheetContent) }
                    )
                },
                floatingActionButton = {
                    Box {
                        floatingActionButton?.invoke()
                    }
                },
                snackbarHost = {
                    Box {
                        snackbarHost(scaffoldState.snackbarHostState)
                    }
                },
                bottomSheetOffset = scaffoldState.bottomSheetState.offset,
                floatingActionButtonPosition = floatingActionButtonPosition
            )
        }
        if(drawerContent == null) {
            child()
        } else {
            ModalDrawer(
                drawerContent = drawerContent,
                drawerState = scaffoldState.drawerState,
                gesturesEnabled = drawerGesturesEnabled,
                drawerShape = drawerShape,
                drawerElevation = drawerElevation,
                drawerBackgroundColor = drawerBackgroundColor,
                drawerContentColor = drawerContentColor,
                scrimColor = drawerScrimColor,
                content = child
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetScaffoldStack(
    body: @Composable () -> Unit,
    bottomSheet: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit,
    snackbarHost: @Composable () -> Unit,
    bottomSheetOffset: State<Float>,
    floatingActionButtonPosition: FabPosition,
) {
    Layout(
        content = {
            body()
            bottomSheet()
            floatingActionButton()
            snackbarHost()
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
            
            val (sheetPlaceable, fabPlaceable, snackbarPlaceable) =
                measurables.drop(1).map {
                    it.measure(constraints.copy(minWidth = 0, minHeight = 0))
                }
            
            val sheetOffsetY = bottomSheetOffset.value.roundToInt()
            
            sheetPlaceable.placeRelative(0, sheetOffsetY)
            
            val fabOffsetX = when(floatingActionButtonPosition) {
                FabPosition.Center -> (placeable.width - fabPlaceable.width) / 2
                else -> placeable.width - fabPlaceable.width - FabEndSpacing.roundToPx()
            }
            val fabOffsetY = sheetOffsetY - fabPlaceable.height / 2
            
            fabPlaceable.placeRelative(fabOffsetX, fabOffsetY)
            
            val snackbarOffsetX = (placeable.width - snackbarPlaceable.width) / 2
            val snackbarOffsetY = placeable.height - snackbarPlaceable.height
            
            snackbarPlaceable.placeRelative(snackbarOffsetX, snackbarOffsetY)
        }
    }
}

private val FabEndSpacing = 16.dp

object BottomSheetScaffoldDefaults {
    
    val SheetElevation = 8.dp
    val SheetPeekHeight = 56.dp
}