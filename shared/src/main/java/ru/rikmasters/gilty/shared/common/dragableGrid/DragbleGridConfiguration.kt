@file:Suppress("KotlinConstantConditions", "DEPRECATION")

package ru.rikmasters.gilty.shared.common.dragableGrid

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Offset.Companion.Zero
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.PointerEventPass.Final
import androidx.compose.ui.input.pointer.PointerEventPass.Main
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

data class ItemPosition(
    val index: Int,
    val key: Any?,
)

internal suspend fun PointerInputScope.awaitLongPressOrCancellation(
    initialDown: PointerInputChange,
): PointerInputChange? {
    var longPress: PointerInputChange? = null
    var currentDown = initialDown
    val longPressTimeout =
        viewConfiguration.longPressTimeoutMillis
    return try {
        withTimeout(longPressTimeout) {
            awaitPointerEventScope {
                var finished = false
                while(!finished) {
                    val event =
                        awaitPointerEvent(Main)
                    if(
                        event.changes.all {
                            it.changedToUpIgnoreConsumed()
                        }
                    ) finished = true
                    if(
                        event.changes.any {
                            it.isConsumed || it.isOutOfBounds(
                                size, extendedTouchPadding
                            )
                        }
                    ) finished = true
                    if(awaitPointerEvent(Final)
                            .changes.any { it.isConsumed }
                    ) finished = true
                    if(!event.isPointerUp(currentDown.id))
                        longPress = event.changes.firstOrNull {
                            it.id == currentDown.id
                        }
                    else event.changes
                        .firstOrNull { it.pressed }
                        ?.let {
                            currentDown = it
                            longPress = currentDown
                        } ?: run { finished = true }
                }
            }
        }
        null
    } catch(_: TimeoutCancellationException) {
        longPress ?: initialDown
    }
}

private fun PointerEvent.isPointerUp(
    pointerId: PointerId,
) = changes.firstOrNull {
    it.id == pointerId
}?.pressed != true

interface DragCancelledAnimation {
    
    suspend fun dragCancelled(
        position: ItemPosition,
        offset: Offset,
    )
    
    val position: ItemPosition?
    val offset: Offset
}

class SpringDragCancelledAnimation(
    private val stiffness: Float =
        Spring.StiffnessMediumLow,
):
    DragCancelledAnimation {
    
    private val animatable =
        Animatable(Zero, Offset.VectorConverter)
    override val offset: Offset
        get() = animatable.value
    
    override var position
            by mutableStateOf<ItemPosition?>(null)
        private set
    
    override suspend fun dragCancelled(
        position: ItemPosition,
        offset: Offset,
    ) {
        this.position = position
        animatable.snapTo(offset)
        animatable.animateTo(
            Zero,
            spring(
                stiffness = stiffness,
                visibilityThreshold = Offset.VisibilityThreshold
            )
        )
        this.position = null
    }
}

@SuppressLint("ReturnFromAwaitPointerEventScope")
fun Modifier.detectReorderAfterLongPress(
    state: ReorderableState<*>,
) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            val down =
                awaitPointerEventScope {
                    awaitFirstDown(
                        requireUnconsumed = false
                    )
                }
            awaitLongPressOrCancellation(down)
                ?.also {
                    state.interactions
                        .trySend(StartDrag(down.id))
                }
        }
    }
)

@Composable
fun rememberReorderableLazyGridState(
    onMove: (ItemPosition, ItemPosition) -> Unit,
    gridState: LazyGridState =
        rememberLazyGridState(),
    canDragOver: ((
        draggedOver: ItemPosition,
        dragging: ItemPosition,
    ) -> Boolean)? = null,
    onDragEnd: ((
        startIndex: Int,
        endIndex: Int,
    ) -> (Unit))? = null,
    maxScrollPerFrame: Dp = 20.dp,
    dragCancelledAnimation: DragCancelledAnimation =
        SpringDragCancelledAnimation(),
): ReorderableLazyGridState {
    val maxScroll = with(LocalDensity.current) {
        maxScrollPerFrame.toPx()
    }
    val scope = rememberCoroutineScope()
    val state = remember(gridState) {
        ReorderableLazyGridState(
            gridState,
            scope,
            maxScroll,
            onMove,
            canDragOver,
            onDragEnd,
            dragCancelledAnimation
        )
    }
    LaunchedEffect(state) {
        state.visibleItemsChanged()
            .collect { state.onDrag(0, 0) }
    }
    
    LaunchedEffect(state) {
        while(true) {
            val diff = state.scrollChannel.receive()
            gridState.scrollBy(diff)
        }
    }
    return state
}

class ReorderableLazyGridState(
    val gridState: LazyGridState,
    scope: CoroutineScope,
    maxScrollPerFrame: Float,
    onMove: (
        fromIndex: ItemPosition,
        toIndex: ItemPosition,
    ) -> (Unit),
    canDragOver: ((
        draggedOver: ItemPosition,
        dragging: ItemPosition,
    ) -> Boolean)? = null,
    onDragEnd: ((
        startIndex: Int,
        endIndex: Int,
    ) -> (Unit))? = null,
    dragCancelledAnimation: DragCancelledAnimation =
        SpringDragCancelledAnimation(),
): ReorderableState<LazyGridItemInfo>(
    scope = scope,
    maxScrollPerFrame =
    maxScrollPerFrame,
    onMove = onMove,
    canDragOver = canDragOver,
    onDragEnd = onDragEnd,
    dragCancelledAnimation =
    dragCancelledAnimation
) {
    
    override val isVerticalScroll: Boolean
        get() = gridState.layoutInfo.orientation ==
                Orientation.Vertical
    override val LazyGridItemInfo.left: Int
        get() = offset.x
    override val LazyGridItemInfo.right: Int
        get() = offset.x + size.width
    override val LazyGridItemInfo.top: Int
        get() = offset.y
    override val LazyGridItemInfo.bottom: Int
        get() = offset.y + size.height
    override val LazyGridItemInfo.width: Int
        get() = size.width
    override val LazyGridItemInfo.height: Int
        get() = size.height
    override val LazyGridItemInfo.itemIndex: Int
        get() = index
    override val LazyGridItemInfo.itemKey: Any
        get() = key
    override val visibleItemsInfo: List<LazyGridItemInfo>
        get() = gridState.layoutInfo.visibleItemsInfo
    override val viewportStartOffset: Int
        get() = gridState.layoutInfo.viewportStartOffset
    override val viewportEndOffset: Int
        get() = gridState.layoutInfo.viewportEndOffset
    override val firstVisibleItemIndex: Int
        get() = gridState.firstVisibleItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = gridState.firstVisibleItemScrollOffset
    
    override suspend fun scrollToItem(
        index: Int, offset: Int,
    ) {
        gridState.scrollToItem(index, offset)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyGridItemScope.ReorderableItem(
    reorderableState: ReorderableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    index: Int? = null,
    content: @Composable BoxScope.(
        isDragging: Boolean,
    ) -> Unit,
) = ReorderableItem(
    state = reorderableState,
    key = key,
    modifier = modifier,
    defaultDraggingModifier = Modifier
        .animateItemPlacement(),
    orientationLocked = false,
    index = index,
    content = content
)

@Composable
fun ReorderableItem(
    state: ReorderableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    defaultDraggingModifier: Modifier = Modifier,
    orientationLocked: Boolean = true,
    index: Int? = null,
    content: @Composable BoxScope.(
        isDragging: Boolean,
    ) -> Unit,
) {
    val isDragging = index
        ?.let { index == state.draggingItemIndex }
        ?: run { key == state.draggingItemKey }
    val draggingModifier =
        if(isDragging) Modifier
            .zIndex(1f)
            .graphicsLayer {
                translationX = if(
                    !orientationLocked
                    || !state.isVerticalScroll
                ) state.draggingItemLeft
                else 0f
                translationY = if(
                    !orientationLocked
                    || state.isVerticalScroll
                ) state.draggingItemTop
                else 0f
            }
        else {
            val cancel = if(index != null)
                index == state.dragCancelledAnimation
                    .position?.index
            else key == state.dragCancelledAnimation
                .position?.key
            if(cancel) Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationX = if(
                        !orientationLocked
                        || !state.isVerticalScroll
                    ) state.dragCancelledAnimation.offset.x
                    else 0f
                    translationY = if(
                        !orientationLocked
                        || state.isVerticalScroll
                    ) state.dragCancelledAnimation.offset.y
                    else 0f
                }
            else defaultDraggingModifier
        }
    Box(modifier = modifier.then(draggingModifier)) {
        content(isDragging)
    }
}

@SuppressLint("ReturnFromAwaitPointerEventScope")
fun Modifier.reorderable(
    state: ReorderableState<*>,
) = then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            val dragStart =
                state.interactions.receive()
            awaitPointerEventScope {
                currentEvent.changes.firstOrNull {
                    it.id == dragStart.id
                }
            }?.let {
                if(state.onDragStart(
                        offsetX = it.position.x.toInt(),
                        offsetY = it.position.y.toInt()
                    )
                ) {
                    dragStart.offset?.apply {
                        state.onDrag(x.toInt(), y.toInt())
                    }
                    detectDrag(
                        down = it.id,
                        onDragEnd = {
                            state.onDragCanceled()
                        },
                        onDragCancel = {
                            state.onDragCanceled()
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            state.onDrag(
                                dragAmount.x.toInt(),
                                dragAmount.y.toInt()
                            )
                        }
                    )
                }
            }
        }
    }
)

internal suspend fun PointerInputScope.detectDrag(
    down: PointerId,
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (
        change: PointerInputChange,
        dragAmount: Offset,
    ) -> Unit,
) {
    awaitPointerEventScope {
        if(
            drag(down) {
                onDrag(it, it.positionChange())
                it.consume()
            }
        ) currentEvent.changes.forEach {
            if(it.changedToUp())
                it.consume()
        }.also { onDragEnd() }
        else onDragCancel()
    }
}

internal data class StartDrag(
    val id: PointerId,
    val offset: Offset? = null,
)

abstract class ReorderableState<T>(
    private val scope: CoroutineScope,
    private val maxScrollPerFrame: Float,
    private val onMove: (
        fromIndex: ItemPosition,
        toIndex: ItemPosition,
    ) -> (Unit),
    private val canDragOver: ((
        draggedOver: ItemPosition,
        dragging: ItemPosition,
    ) -> Boolean)?,
    private val onDragEnd: ((
        startIndex: Int,
        endIndex: Int,
    ) -> (Unit))?,
    val dragCancelledAnimation: DragCancelledAnimation,
) {
    
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set
    val draggingItemKey: Any?
        get() = selected?.itemKey
    protected abstract val T.left: Int
    protected abstract val T.top: Int
    protected abstract val T.right: Int
    protected abstract val T.bottom: Int
    protected abstract val T.width: Int
    protected abstract val T.height: Int
    protected abstract val T.itemIndex: Int
    protected abstract val T.itemKey: Any
    protected abstract val visibleItemsInfo: List<T>
    protected abstract val firstVisibleItemIndex: Int
    protected abstract val firstVisibleItemScrollOffset: Int
    protected abstract val viewportStartOffset: Int
    protected abstract val viewportEndOffset: Int
    internal val interactions = Channel<StartDrag>()
    internal val scrollChannel = Channel<Float>()
    val draggingItemLeft: Float
        get() = draggingLayoutInfo?.let { item ->
            (selected?.left ?: 0) + draggingDelta.x - item.left
        } ?: 0f
    val draggingItemTop: Float
        get() = draggingLayoutInfo?.let { item ->
            (selected?.top ?: 0) + draggingDelta.y - item.top
        } ?: 0f
    abstract val isVerticalScroll: Boolean
    private val draggingLayoutInfo: T?
        get() = visibleItemsInfo
            .firstOrNull { it.itemIndex == draggingItemIndex }
    private var draggingDelta by mutableStateOf(Zero)
    private var selected by mutableStateOf<T?>(null)
    private var autoscroller: Job? = null
    private val targets = mutableListOf<T>()
    private val distances = mutableListOf<Int>()
    
    protected abstract suspend fun scrollToItem(
        index: Int, offset: Int,
    )
    
    @OptIn(ExperimentalCoroutinesApi::class)
    internal fun visibleItemsChanged() =
        snapshotFlow { draggingItemIndex != null }
            .flatMapLatest {
                if(it) snapshotFlow { visibleItemsInfo }
                else flowOf(null)
            }
            .filterNotNull()
            .distinctUntilChanged { old, new ->
                old.firstOrNull()?.itemIndex ==
                        new.firstOrNull()?.itemIndex
                        && old.count() == new.count()
            }
    
    internal open fun onDragStart(
        offsetX: Int,
        offsetY: Int,
    ): Boolean {
        val x: Int
        val y: Int
        if(isVerticalScroll) {
            x = offsetX
            y = offsetY + viewportStartOffset
        } else {
            x = offsetX + viewportStartOffset
            y = offsetY
        }
        return visibleItemsInfo
            .firstOrNull {
                x in it.left..it.right
                        && y in it.top..it.bottom
            }
            ?.also {
                selected = it
                draggingItemIndex = it.itemIndex
            } != null
    }
    
    internal fun onDragCanceled() {
        val dragIdx = draggingItemIndex
        if(dragIdx != null) {
            val position = ItemPosition(
                index = dragIdx,
                key = selected?.itemKey
            )
            val offset = Offset(
                x = draggingItemLeft,
                y = draggingItemTop
            )
            scope.launch {
                dragCancelledAnimation
                    .dragCancelled(
                        position = position,
                        offset = offset
                    )
            }
        }
        selected = null
        draggingDelta = Zero
        draggingItemIndex = null
        cancelAutoScroll()
        onDragEnd?.apply {
            selected?.itemIndex?.let { start ->
                draggingItemIndex?.let { end ->
                    invoke(start, end)
                }
            }
        }
    }
    
    internal fun onDrag(offsetX: Int, offsetY: Int) {
        val selected = selected ?: return
        draggingDelta = Offset(
            x = draggingDelta.x + offsetX,
            y = draggingDelta.y + offsetY
        )
        val draggingItem =
            draggingLayoutInfo ?: return
        val startOffset =
            draggingItem.top + draggingItemTop
        val startOffsetX =
            draggingItem.left + draggingItemLeft
        chooseDropItem(
            draggedItemInfo = draggingItem,
            items = findTargets(
                x = draggingDelta.x.toInt(),
                y = draggingDelta.y.toInt(),
                selected = selected
            ),
            curX = startOffsetX.toInt(),
            curY = startOffset.toInt()
        )?.also { targetItem ->
            if(
                targetItem.itemIndex == firstVisibleItemIndex
                || draggingItem.itemIndex == firstVisibleItemIndex
            ) {
                scope.launch {
                    onMove.invoke(
                        ItemPosition(
                            index = draggingItem.itemIndex,
                            key = draggingItem.itemKey
                        ),
                        ItemPosition(
                            index = targetItem.itemIndex,
                            key = targetItem.itemKey
                        )
                    )
                    scrollToItem(
                        index = firstVisibleItemIndex,
                        offset = firstVisibleItemScrollOffset
                    )
                }
            } else onMove.invoke(
                ItemPosition(
                    index = draggingItem.itemIndex,
                    key = draggingItem.itemKey
                ),
                ItemPosition(
                    index = targetItem.itemIndex,
                    key = targetItem.itemKey
                )
            )
            draggingItemIndex = targetItem.itemIndex
        }
        
        with(
            calcAutoScrollOffset(0, maxScrollPerFrame)
        ) { if(this != 0f) autoscroll(this) }
    }
    
    private fun autoscroll(scrollOffset: Float) {
        if(scrollOffset != 0f) {
            if(autoscroller?.isActive == true) return
            autoscroller = scope.launch {
                var scroll = scrollOffset
                var start = 0L
                while(
                    scroll != 0f
                    && autoscroller?.isActive == true
                ) {
                    withFrameMillis {
                        if(start == 0L) start = it
                        else scroll =
                            calcAutoScrollOffset(
                                it - start,
                                maxScrollPerFrame
                            )
                    }
                    scrollChannel.trySend(scroll)
                }
            }
        } else cancelAutoScroll()
    }
    
    private fun cancelAutoScroll() {
        autoscroller?.cancel()
        autoscroller = null
    }
    
    protected open fun findTargets(
        x: Int,
        y: Int,
        selected: T,
    ): List<T> {
        targets.clear()
        distances.clear()
        val left = x + selected.left
        val right = x + selected.right
        val top = y + selected.top
        val bottom = y + selected.bottom
        val centerX = (left + right) / 2
        val centerY = (top + bottom) / 2
        visibleItemsInfo.forEach { item ->
            if(
                item.itemIndex == draggingItemIndex
                || item.bottom < top
                || item.top > bottom
                || item.right < left
                || item.left > right
            ) return@forEach
            if(canDragOver?.invoke(
                    ItemPosition(
                        index = item.itemIndex,
                        key = item.itemKey
                    ),
                    ItemPosition(
                        index = selected.itemIndex,
                        key = selected.itemKey
                    )
                ) != false
            ) {
                val dx =
                    (centerX - (item.left + item.right) / 2)
                        .absoluteValue
                val dy =
                    (centerY - (item.top + item.bottom) / 2)
                        .absoluteValue
                val dist = dx * dx + dy * dy
                var pos = 0
                for(j in targets.indices) {
                    if(dist > distances[j]) {
                        pos++
                    } else {
                        break
                    }
                }
                targets.add(pos, item)
                distances.add(pos, dist)
            }
        }
        return targets
    }
    
    protected open fun chooseDropItem(
        draggedItemInfo: T?,
        items: List<T>,
        curX: Int,
        curY: Int,
    ): T? {
        if(draggedItemInfo == null)
            return if(draggingItemIndex != null)
                items.lastOrNull() else null
        var target: T? = null
        var highScore = -1
        val right = curX + draggedItemInfo.width
        val bottom = curY + draggedItemInfo.height
        val dx = curX - draggedItemInfo.left
        val dy = curY - draggedItemInfo.top
        
        items.forEach { item ->
            if(dx > 0) {
                val diff = item.right - right
                if(diff < 0 && item.right > draggedItemInfo.right) {
                    val score = diff.absoluteValue
                    if(score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }
            if(dx < 0) {
                val diff = item.left - curX
                if(diff > 0 && item.left < draggedItemInfo.left) {
                    val score = diff.absoluteValue
                    if(score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }
            if(dy < 0) {
                val diff = item.top - curY
                if(diff > 0 && item.top < draggedItemInfo.top) {
                    val score = diff.absoluteValue
                    if(score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }
            if(dy > 0) {
                val diff = item.bottom - bottom
                if(diff < 0 && item.bottom > draggedItemInfo.bottom) {
                    val score = diff.absoluteValue
                    if(score > highScore) {
                        highScore = score
                        target = item
                    }
                }
            }
        }
        return target
    }
    
    private fun calcAutoScrollOffset(
        time: Long, maxScroll: Float,
    ): Float {
        val draggingItem =
            draggingLayoutInfo ?: return 0f
        val startOffset: Float
        val endOffset: Float
        val delta: Float
        if(isVerticalScroll) {
            startOffset = draggingItem.top + draggingItemTop
            endOffset = startOffset + draggingItem.height
            delta = draggingDelta.y
        } else {
            startOffset = draggingItem.left + draggingItemLeft
            endOffset = startOffset + draggingItem.width
            delta = draggingDelta.x
        }
        return when {
            delta > 0 -> (endOffset - viewportEndOffset)
                .coerceAtLeast(0f)
            delta < 0 -> (startOffset - viewportStartOffset)
                .coerceAtMost(0f)
            else -> 0f
        }.let {
            interpolateOutOfBoundsScroll(
                viewSize = (endOffset - startOffset).toInt(),
                viewSizeOutOfBounds = it,
                time = time,
                maxScroll = maxScroll
            )
        }
    }
    
    companion object {
        
        private const val ACCELERATION_LIMIT_TIME_MS: Long = 1500
        private val EaseOutQuadInterpolator: (Float) -> (Float) = {
            val t = 1 - it
            1 - t * t * t * t
        }
        
        private val EaseInQuintInterpolator: (Float) -> (Float) = {
            it * it * it * it * it
        }
        
        private fun interpolateOutOfBoundsScroll(
            viewSize: Int,
            viewSizeOutOfBounds: Float,
            time: Long,
            maxScroll: Float,
        ): Float {
            if(viewSizeOutOfBounds == 0f) return 0f
            val outOfBoundsRatio = min(
                1f, 1f * viewSizeOutOfBounds
                    .absoluteValue / viewSize
            )
            val cappedScroll =
                sign(viewSizeOutOfBounds) *
                        maxScroll *
                        EaseOutQuadInterpolator(
                            outOfBoundsRatio
                        )
            val timeRatio =
                if(time > ACCELERATION_LIMIT_TIME_MS) 1f
                else time.toFloat() / ACCELERATION_LIMIT_TIME_MS
            return (cappedScroll * EaseInQuintInterpolator(timeRatio))
                .let {
                    if(it == 0f)
                        if(viewSizeOutOfBounds > 0)
                            1f else -1f
                    else it
                }
        }
    }
}