@file:Suppress("unused")

package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.common.dragGrid.ReorderableItem
import ru.rikmasters.gilty.shared.common.dragGrid.detectReorderAfterLongPress
import ru.rikmasters.gilty.shared.common.dragGrid.rememberReorderableLazyGridState
import ru.rikmasters.gilty.shared.common.dragGrid.reorderable

class DragableViewModel: ViewModel()

data class ItemData(
    val title: String,
    val key: String,
    val isLocked: Boolean = false,
)

@Composable
fun DragTable() {
    
    var dogs by remember {
        mutableStateOf(
            List(100) {
                if(it.mod(10) == 0)
                    ItemData(
                        "Locked",
                        "id$it",
                        true
                    )
                else ItemData(
                    "Dog $it",
                    "id$it"
                )
            }
        )
    }
    
    val context = LocalContext.current
    
    DragableGrid(
        list = dogs,
        onDrag = { from, to ->
            dogs = dogs.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            Toast.makeText(
                context,
                "$from :: $to",
                Toast.LENGTH_LONG
            ).show()
        },
        canDragOver = {
            dogs.getOrNull(it.index)?.isLocked != true
        },
        onDragEnd = { s, e ->
            Toast.makeText(
                context,
                "$s :: $e",
                Toast.LENGTH_LONG
            ).show()
        },
        blockedItem = {
            Box(
                contentAlignment = Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(colors.surface)
            ) { Text("Locked") }
        },
        dragableItem = {}
    )
}

@Suppress("unused")
@Composable
fun DragableGrid(
    list: List<ItemData>,
    onDrag: (ItemPosition, ItemPosition) -> Unit,
    canDragOver: (draggedOver: ItemPosition) -> Boolean,
    onDragEnd: (Int, Int) -> Unit,
    blockedItem: (@Composable () -> Unit)? = null,
    @Suppress("UNUSED_PARAMETER") dragableItem: @Composable () -> Unit,
) {
    val state =
        rememberReorderableLazyGridState(
            onMove = { from, to -> onDrag(from, to) },
            canDragOver = { draggedOver, _ ->
                canDragOver(draggedOver)
            },
            onDragEnd = { s, e -> onDragEnd(s, e) }
        )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = state.gridState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.reorderable(state)
    ) {
        blockedItem?.let { item { it.invoke() } }
        items(list, { it.key }) { item ->
            ReorderableItem(
                reorderableState = state,
                key = item.key
            ) { isDragging ->
                Box(
                    contentAlignment = Center,
                    modifier = Modifier
                        .detectReorderAfterLongPress(state)
                        .shadow(
                            animateDpAsState(
                                if(isDragging)
                                    8.dp else 0.dp
                            ).value
                        )
                        .aspectRatio(1f)
                        .background(colors.primary)
                ) { Text(item.title) }
            }
        }
    }
}