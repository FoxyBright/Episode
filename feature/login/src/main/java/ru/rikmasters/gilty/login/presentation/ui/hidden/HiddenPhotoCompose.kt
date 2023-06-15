package ru.rikmasters.gilty.login.presentation.ui.hidden

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.screenWidth
import ru.rikmasters.gilty.shared.theme.Colors
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun HiddenPhotoPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            HiddenContent(
                HiddenState(listOf(), 0, false)
            )
        }
    }
}

data class HiddenState(
    val photoList: List<AvatarModel>,
    val photosAmount: Int,
    val photoViewState: Boolean,
    val viewerImages: List<AvatarModel?> = emptyList(),
    val viewerSelectImage: AvatarModel? = null,
    val viewerMenuState: Boolean = false,
    val viewerType: PhotoViewType = PhotoViewType.PHOTO,
)

interface HiddenCallback {

    fun onNext() {}
    fun onBack() {}
    fun onSelectImage(image: AvatarModel) {}
    fun onDeleteImage(image: AvatarModel) {}
    fun openGallery() {}
    fun onPhotoViewDismiss(state: Boolean)
    fun onPhotoViewChangeMenuState(state: Boolean) = Unit
    fun onPhotoViewMenuItemClick(imageId: String) = Unit
    fun onPhotoMoved(from: ItemPosition, to: ItemPosition) = Unit
}

@Composable
fun HiddenContent(
    state: HiddenState,
    modifier: Modifier = Modifier,
    callback: HiddenCallback? = null,
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

    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
    ) {
        ActionBar(
            stringResource(R.string.profile_hidden_photo),
            Modifier.padding(bottom = 20.dp),
            stringResource(R.string.profile_hidden_photo_label),
            extra = if (state.photosAmount == 0) null
            else stringResource(R.string.profile_hidden_photo_amount, state.photosAmount)
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
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .reorderable(stateDragable),
            verticalArrangement = spacedBy(4.dp),
            horizontalArrangement = spacedBy(4.dp),
            state = stateDragable.gridState,
        ) {
            item {
                GalleryButton(
                    modifier = Modifier
                        .aspectRatio(1f),
                    callback = callback
                )
            }
            items(items = state.photoList, key = { it.thumbnail.url }) { img ->
                ReorderableItem(
                    reorderableState = stateDragable,
                    key = img.thumbnail.url,
                    modifier = Modifier

                ) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)
                    LazyItem(
                        image = img.thumbnail.url,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .detectReorderAfterLongPress(stateDragable)
                            .clip(shapes.large)
                            .shadow(elevation.value),
                        onSelect = { callback?.onSelectImage(img) },
                        onDelete = { callback?.onDeleteImage(img) }
                    )
                }
            }
        }
    }
/*    Box(Modifier.fillMaxSize()) {
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }*/
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GalleryButton(modifier: Modifier = Modifier, callback: HiddenCallback?) {
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
                    (null), Modifier
                        .padding(6.dp)
                        .size(32.dp), White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LazyItem(
    image: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    Box(
        modifier
            .size(130.dp)
            .clip(shapes.small)
            .clickable { onSelect(image) },
        TopEnd
    ) {
        GCachedImage(
            image, Modifier.fillMaxSize(),
            contentScale = Crop,
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
                    (null), Modifier.fillMaxSize()
                )
                Icon(
                    Filled.Close, (null),
                    Modifier.size(16.dp), White
                )
            }
        }
    }
}