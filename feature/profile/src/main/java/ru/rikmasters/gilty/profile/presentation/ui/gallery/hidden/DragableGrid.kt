package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.common.dragableGrid.*


@Suppress("unused")
@Composable
fun DragableGrid() {
    
    val vm = ReorderListViewModel()
    
    val state =
        rememberReorderableLazyGridState(
            onMove = vm::moveDog,
            canDragOver = { draggedOver, _ ->
                vm.isDogDragEnabled(draggedOver)
            }
        )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        state = state.gridState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.reorderable(state)
    ) {
        items(vm.dogs, { it.key }) { item ->
            if(item.isLocked) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colors.surface)
                ) { Text(item.title) }
            } else {
                ReorderableItem(state, item.key) { isDragging ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .detectReorderAfterLongPress(state)
                            .shadow(
                                animateDpAsState(
                                    if(isDragging)
                                        8.dp else 0.dp
                                ).value
                            )
                            .aspectRatio(1f)
                            .background(MaterialTheme.colors.primary)
                    ) { Text(item.title) }
                }
            }
        }
    }
}

class ReorderListViewModel {
    
    var dogs by mutableStateOf(
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
    
    fun moveDog(from: ItemPosition, to: ItemPosition) {
        dogs = dogs.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }
    
    fun isDogDragEnabled(draggedOver: ItemPosition) =
        dogs.getOrNull(draggedOver.index)?.isLocked != true
}

data class ItemData(
    val title: String,
    val key: String,
    val isLocked: Boolean = false,
)