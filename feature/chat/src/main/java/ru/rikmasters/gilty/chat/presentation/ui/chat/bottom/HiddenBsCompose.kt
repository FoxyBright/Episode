package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import ru.rikmasters.gilty.gallery.gallery.GalleryCallback
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.common.extentions.items
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.*

data class HiddenGalleryState(
    val type: GalleryImageType,
    val images: LazyPagingItems<AvatarModel>,
    val selectedImages: List<AvatarModel>? = null,
    val menuState: Boolean = false,
    val menuItems: List<String>? = null,
    val isOnline: Boolean = false,
    val buttonLabel: String = "",
    val buttonEnabled: Boolean = false,
)

@Composable
fun HiddenGalleryGrid(
    modifier: Modifier = Modifier,
    images: LazyPagingItems<AvatarModel>,
    selected: List<AvatarModel>?,
    type: GalleryImageType,
    callback: GalleryCallback?,
) {
    val cells = 3
    LazyVerticalGrid(
        GridCells.Fixed(cells),
        modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if(images.loadState.refresh is LoadState.Loading) {
            item(span = { GridItemSpan(cells) }) {
                PagingLoader(images.loadState)
            }
        }
        items(images) { image ->
            image?.let {
                HiddenGalleryImage(
                    it, type,
                    selected?.contains(it),
                    Modifier, callback
                )
            }
        }
        if(images.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(cells) }) {
                PagingLoader(images.loadState)
            }
        }
        items(cells) { Spacer(Modifier.size(115.dp)) }
    }
}

@Composable
fun HiddenGalleryImage(
    image: AvatarModel,
    type: GalleryImageType,
    selected: Boolean?,
    modifier: Modifier = Modifier,
    callback: GalleryCallback? = null,
) {
    Box(
        modifier
            .size(115.dp)
            .clickable {
                callback?.onHiddenImageClick(image)
            }
    ) {
        GCachedImage(
            image.thumbnail.url,
            Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if(type == GalleryImageType.MULTIPLE) CheckBox(
            (selected ?: false),
            Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) { callback?.onHiddenImageClick(image) }
    }
}

@Composable
fun HiddenBsContent(
    state: HiddenGalleryState,
    modifier: Modifier,
    callback: GalleryCallback? = null,
) {
    when {
        state.images.loadState.refresh is LoadState.Error -> {}
        state.images.loadState.append is LoadState.Error -> {}
        else -> {
            Column(
                modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
            ) {
                Text(
                    stringResource(R.string.profile_hidden_photo),
                    Modifier.padding(bottom = 14.dp),
                    style = typography.labelLarge
                )
                HiddenGalleryGrid(
                    Modifier.weight(1f),
                    state.images,
                    state.selectedImages,
                    state.type,
                    callback
                )
                GradientButton(
                    Modifier.padding(vertical = 28.dp),
                    state.buttonLabel,
                    state.buttonEnabled,
                    state.isOnline
                ) { callback?.onAttach() }
            }
            if(state.images.itemCount == 0
                && state.images.loadState.refresh
                        is LoadState.NotLoading
            ) {
                EmptyHiddenBs()
            }
        }
    }
}

@Composable
private fun EmptyHiddenBs(
    modifier: Modifier = Modifier,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            stringResource(R.string.profile_hidden_photo),
            modifier.align(TopStart),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        EmptyScreen(
            stringResource(R.string.chats_empty_hidden_photos),
            R.drawable.ic_image_box,
            Modifier,
            50.dp,
            4.dp
        )
    }
}
