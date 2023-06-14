@file:OptIn(ExperimentalMaterial3Api::class)

package ru.rikmasters.gilty.shared.shared.bottomsheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.FabPosition.Companion.Center
import androidx.compose.material3.FabPosition.Companion.End
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.*
import androidx.compose.ui.layout.Placeable.PlacementScope
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.core.util.composable.getDensity
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue.Collapsed
import ru.rikmasters.gilty.shared.shared.bottomsheet.BottomSheetValue.Expanded
import ru.rikmasters.gilty.shared.shared.bottomsheet.DrawerDefaults.Elevation
import ru.rikmasters.gilty.shared.shared.bottomsheet.SwipeableDefaults.AnimationSpec
import kotlin.math.roundToInt

@Stable
enum class BottomSheetValue {
    
    Collapsed, Expanded
}

@Stable
class BottomSheetState(
    initialValue: BottomSheetValue,
    animationSpec: AnimationSpec<Float> = AnimationSpec,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true },
): SwipeableState<BottomSheetValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    
    val isCollapsed get() = currentValue == Collapsed
    val isExpanded get() = currentValue == Expanded
    suspend fun expand() = animateTo(Expanded)
    suspend fun collapse() = animateTo(Collapsed)
    
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
    
    internal val nestedScrollConnection =
        this.PreUpPostDownNestedScrollConnection
}

@Composable
fun rememberBottomSheetState(
    initialValue: BottomSheetValue,
    animationSpec: AnimationSpec<Float> = AnimationSpec,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true },
) = rememberSaveable(
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

@Stable
class BottomSheetScaffoldState(
    val drawerState: DrawerState,
    val bottomSheetState: BottomSheetState,
    val snackbarHostState: SnackbarHostState,
)

@Composable
fun rememberBottomSheetScaffoldState(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    bottomSheetState: BottomSheetState = rememberBottomSheetState(
        Collapsed
    ),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = remember(drawerState, bottomSheetState, snackbarHostState) {
    BottomSheetScaffoldState(
        drawerState = drawerState,
        bottomSheetState = bottomSheetState,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun BottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    topBar: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit =
        { SnackbarHost(it) },
    floatingActionButton: (@Composable () -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = End,
    sheetGesturesEnabled: Boolean = true,
    sheetShape: Shape = RoundedCornerShape(24.dp),
    sheetBackgroundColor: Color = colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPeekHeight: Dp = 56.dp,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = shapes.large,
    drawerElevation: Dp = Elevation,
    drawerBackgroundColor: Color = colorScheme.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    drawerScrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(getDensity()) { sheetPeekHeight.toPx() }
        var bottomSheetHeight by remember { mutableStateOf(fullHeight) }
        val bsState = scaffoldState.bottomSheetState
        val mod = Modifier
            .nestedScroll(bsState.nestedScrollConnection)
            .swipeable(
                state = bsState,
                anchors = mapOf(
                    fullHeight - peekHeightPx to Collapsed,
                    fullHeight - bottomSheetHeight to Expanded,
                ),
                orientation = Vertical,
                enabled = sheetGesturesEnabled,
                resistance = null
            )
            .semantics {
                if(peekHeightPx != bottomSheetHeight) when {
                    bsState.isCollapsed -> expand {
                        if(scaffoldState set Expanded)
                            scope.launch { bsState.expand() }
                        true
                    }
                    else -> collapse {
                        if(scaffoldState set Collapsed)
                            scope.launch { bsState.collapse() }
                        true
                    }
                }
            }
            .fillMaxWidth()
            .requiredHeightIn(sheetPeekHeight)
            .onGloballyPositioned {
                bottomSheetHeight =
                    it.size.height.toFloat()
            }
        
        val child = child(
            modifier = mod,
            sheetShape = sheetShape,
            topBar = topBar,
            content = content,
            sheetPeekHeight = sheetPeekHeight,
            sheetContent = sheetContent,
            floatingActionButton = floatingActionButton,
            snackbarHost = snackbarHost,
            scaffoldState = scaffoldState,
            floatingActionButtonPosition = floatingActionButtonPosition
        )
        
        drawerContent?.let {
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
        } ?: child()
    }
}

private fun child(
    modifier: Modifier,
    sheetShape: Shape,
    topBar: (@Composable () -> Unit)?,
    content: @Composable (PaddingValues) -> Unit,
    sheetPeekHeight: Dp,
    sheetContent: @Composable ColumnScope.() -> Unit,
    floatingActionButton: (@Composable () -> Unit)?,
    snackbarHost: @Composable (SnackbarHostState) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    floatingActionButtonPosition: FabPosition,
) = @Composable {
    BottomSheetScaffoldStack(
        body = {
            Surface(
                color = colorScheme.background,
                contentColor = contentColorFor(colorScheme.background)
            ) {
                Column(Modifier.fillMaxSize()) {
                    topBar?.invoke()
                    content(PaddingValues(bottom = sheetPeekHeight))
                }
            }
        },
        bottomSheet = {
            Surface(
                modifier = modifier,
                shape = sheetShape,
                color = Transparent,
                contentColor = contentColorFor(Transparent),
                content = { Column(content = sheetContent) }
            )
        },
        floatingActionButton = {
            Box { floatingActionButton?.invoke() }
        },
        snackbarHost = {
            Box { snackbarHost(scaffoldState.snackbarHostState) }
        },
        bottomSheetOffset = scaffoldState.bottomSheetState.offset,
        floatingActionButtonPosition = floatingActionButtonPosition
    )
}

infix fun BottomSheetScaffoldState.set(
    state: BottomSheetValue,
) = bottomSheetState.confirmStateChange(state)

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
        },
        measurePolicy = { measure, const ->
            measurePolicy(
                measure = measure,
                const = const,
                bottomSheetOffset = bottomSheetOffset,
                floatingActionButtonPosition =
                floatingActionButtonPosition
            )
        }
    )
}

private fun MeasureScope.measurePolicy(
    measure: List<Measurable>,
    const: Constraints,
    bottomSheetOffset: State<Float>,
    floatingActionButtonPosition: FabPosition,
) = measure
    .first()
    .measure(const)
    .let { placeable ->
        layout(
            width = placeable.width,
            height = placeable.height
        ) {
            layoutSettings(
                placeable = placeable,
                measurables = measure,
                constraints = const,
                bottomSheetOffset = bottomSheetOffset,
                floatingActionButtonPosition =
                floatingActionButtonPosition
            )
        }
    }

private fun PlacementScope.layoutSettings(
    placeable: Placeable,
    measurables: List<Measurable>,
    constraints: Constraints,
    bottomSheetOffset: State<Float>,
    floatingActionButtonPosition: FabPosition,
) {
    placeable.placeRelative(0, 0)
    
    val (
        sheetPlaceable,
        fabPlaceable,
        snackbarPlaceable,
    ) = measurables.drop(1).map {
        it.measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0
            )
        )
    }
    
    bottomSheetOffset
        .value
        .roundToInt()
        .let {
            sheetPlaceable(sheetPlaceable, it)
            fabPlaceable(
                fabPlaceable = fabPlaceable,
                placeable = placeable,
                floatingActionButtonPosition =
                floatingActionButtonPosition,
                sheetOffsetY = it
            )
        }
    snackbarPlaceable(snackbarPlaceable, placeable)
}

private fun PlacementScope.sheetPlaceable(
    sheetPlaceable: Placeable,
    sheetOffsetY: Int,
) = sheetPlaceable.placeRelative(0, sheetOffsetY)

private fun PlacementScope.fabPlaceable(
    fabPlaceable: Placeable,
    placeable: Placeable,
    floatingActionButtonPosition: FabPosition,
    sheetOffsetY: Int,
) = fabPlaceable.placeRelative(
    x = getFabOffset(
        floatingActionButtonPosition =
        floatingActionButtonPosition,
        placeable = placeable,
        fabPlaceable = fabPlaceable
    ),
    y = sheetOffsetY - fabPlaceable.height / 2
)

private fun PlacementScope.snackbarPlaceable(
    snackbarPlaceable: Placeable,
    placeable: Placeable,
) = snackbarPlaceable.placeRelative(
    (placeable.width - snackbarPlaceable.width) / 2,
    placeable.height - snackbarPlaceable.height
)


private fun getFabOffset(
    floatingActionButtonPosition: FabPosition,
    placeable: Placeable,
    fabPlaceable: Placeable,
) = when(floatingActionButtonPosition) {
    Center -> (placeable.width - fabPlaceable.width) / 2
    else -> placeable.width - fabPlaceable.width - 16
}