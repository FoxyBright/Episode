package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import ru.rikmasters.gilty.gallery.photoview.PhotoView
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.common.dragGrid.ReorderableItem
import ru.rikmasters.gilty.shared.common.dragGrid.detectReorderAfterLongPress
import ru.rikmasters.gilty.shared.common.dragGrid.rememberReorderableLazyGridState
import ru.rikmasters.gilty.shared.common.dragGrid.reorderable
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.PagingLoader
import ru.rikmasters.gilty.shared.shared.screenWidth
import ru.rikmasters.gilty.shared.theme.Colors
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

interface HiddenBsCallback {

    fun onSelectImage(image: AvatarModel) {}
    fun onDeleteImage(image: AvatarModel) {}
    fun openGallery() {}
    fun onBack() {}
    fun onPhotoViewDismiss(state: Boolean)
    fun onPhotoViewChangeMenuState(state: Boolean) = Unit
    fun onPhotoViewMenuItemClick(imageId: String) = Unit
    fun onPhotoMoved(from: ItemPosition, to: ItemPosition) = Unit
}

data class HiddenBsState(
    val photoList: LazyPagingItems<AvatarModel>,
    val photoAmount: Int,
    val photoViewState: Boolean,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val viewerType: PhotoViewType = PhotoViewType.PHOTO,
)

@Composable
fun HiddenBsContent(
    state: HiddenBsState,
    modifier: Modifier = Modifier,
    callback: HiddenBsCallback? = null,
) {
    if (state.photoViewState) PhotoView(
        images = state.viewerImages,
        selected = state.viewerSelectImage,
        menuState = state.viewerMenuState,
        type = state.viewerType,
        onMenuClick = { callback?.onPhotoViewChangeMenuState(it) },
        onMenuItemClick = { callback?.onPhotoViewMenuItemClick(it) },
        onBack = { callback?.onPhotoViewDismiss(false) },
    )

    Column(modifier) {
        ActionBar(
            title = stringResource(R.string.profile_hidden_photo),
            modifier = Modifier.padding(bottom = 20.dp),
            extra = if (state.photoAmount == 0) null
            else stringResource(R.string.profile_hidden_photo_amount, state.photoAmount)
        ) { callback?.onBack() }

        val stateDragable = rememberReorderableLazyGridState(
            onMove = { from, to ->
                callback?.onPhotoMoved(from, to)
            },
            canDragOver = { draggedOver, dragging ->
                draggedOver.index != 0
            }
        )

        LazyVerticalGrid(
            GridCells.Fixed(3), Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .reorderable(stateDragable),
            state = stateDragable.gridState,
            verticalArrangement = spacedBy(4.dp),
            horizontalArrangement = spacedBy(4.dp)
        ) {
            when {
                state.photoList.loadState.refresh is LoadState.Error -> {}
                state.photoList.loadState.append is LoadState.Error -> {}
                state.photoList.loadState.refresh is LoadState.Loading -> {
                    item(span = { GridItemSpan(3) }) {
                        PagingLoader(state.photoList.loadState)
                    }
                }

                else -> {
                    item {
                        GalleryButton(
                            modifier = Modifier
                                .aspectRatio(1f),
                            callback = callback
                        )
                    }
                    items(items = state.viewerImages, key = { it?.id ?: "" }) { img ->
                        img?.let {
                            ReorderableItem(
                                reorderableState = stateDragable,
                                key = img.id,
                                modifier = Modifier
                            ) { isDragging ->
                                val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)
                                LazyItem(
                                    image = img.thumbnail.url,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .detectReorderAfterLongPress(stateDragable)
                                        .clip(shapes.large)
                                        .zIndex(if(isDragging) 1f else 2f)
                                        .shadow(elevation.value),
                                    onSelect = { callback?.onSelectImage(img) },
                                    onDelete = { callback?.onDeleteImage(img) }
                                )
                            }
                        }
                    }
                   if (state.photoList.loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(3) }) {
                            PagingLoader(state = state.photoList.loadState)
                        }
                    }

                    /*items(3) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )
                    }*/
                }
            }
        }
        /*LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = stateDragable.gridState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.reorderable(stateDragable)
        ) {
            items(vm.dogs, { it.key }) { item ->
                ReorderableItem(stateDragable, item.key) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .detectReorderAfterLongPress(stateDragable)
                            .shadow(elevation.value)
                            .aspectRatio(1f)
                            .background(Color.Red)
                    ) {
                        Text(item.title)
                    }

                }
            }
        }*/
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GalleryButton(
    modifier: Modifier,
    callback: HiddenBsCallback?,
) {
    Card(
        onClick = { callback?.openGallery() },
        modifier = modifier
            .size((screenWidth.dp - 72.dp) / 3)
            .clip(shapes.large),
        shape = shapes.large,
        colors = cardColors(Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.grayButton),
            Center
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(Colors.AlmostRed)
            ) {
                Icon(
                    painterResource(R.drawable.ic_image_box),
                    (null),
                    Modifier
                        .padding(8.dp)
                        .size(32.dp),
                    White
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LazyItem(
    image: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    Box(
        modifier
            // .fillMaxSize()
            .clip(shapes.large)
            .clickable { onSelect(image) },
        TopEnd
    ) {
        GCachedImage(
            image, Modifier.fillMaxSize(),
            contentScale = Crop
        )
        Card(
            { onDelete(image) },
            Modifier
                .padding(4.dp)
                .size(26.dp)
                .clip(CircleShape)
                .align(TopEnd)
                .alpha(50f),
            colors = cardColors(Transparent)
        ) {
            Box(Modifier, Center) {
                Image(
                    painterResource(R.drawable.transparency_circle),
                    (null),
                    Modifier.fillMaxSize()
                )
                Icon(
                    Icons.Filled.Close,
                    (null),
                    Modifier.size(16.dp),
                    White
                )
            }
        }
    }
}
