package ru.rikmasters.gilty.gallery.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.switch

data class GalleryState(
    val type: GalleryImageType,
    val images: List<String> = mutableListOf(),
    val selectedImages: List<String>? = null,
    val menuState: Boolean = false,
    val menuItems: List<String>? = null,
    val isOnline: Boolean = false,
    val buttonLabel: String = "",
    val buttonEnabled: Boolean = false,
)

interface GalleryCallback {
    
    fun onHiddenImageClick(image: AvatarModel) {}
    fun onMenuDismiss(state: Boolean) {}
    fun onImageClick(image: String) {}
    fun onMenuItemClick(item: Int) {}
    fun onAttach() {}
    fun onBack() {}
}

@Composable
fun GalleryTopBar(
    menuState: Boolean,
    callback: GalleryCallback?,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 26.dp),
        SpaceBetween
    ) {
        Row(Modifier, Start, CenterVertically) {
            RowActionBar(stringResource(R.string.profile_gallery_title)) {
                callback?.onBack()
            }
            IconButton(
                { callback?.onMenuDismiss(true) },
                Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if(menuState)
                        Filled.KeyboardArrowUp
                    else Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun GalleryGrid(
    modifier: Modifier = Modifier,
    images: List<String>,
    selected: List<String>?,
    type: GalleryImageType,
    isWeb: Boolean = false,
    callback: GalleryCallback?,
) {
    val columns = 3
    LazyVerticalGrid(
        columns = Fixed(columns),
        modifier = modifier.padding(horizontal = 4.dp),
        verticalArrangement = spacedBy(4.dp),
        horizontalArrangement = spacedBy(4.dp)
    ) {
        itemsIndexed(
            items = images,
            key = { i, it -> "$i$it" },
            contentType = { i, it -> "$i$it" }
        ) { _, image ->
            GalleryImage(
                image = image,
                type = type,
                selected = selected?.contains(image),
                isWeb = isWeb,
                callback = callback
            )
        }
        items(columns) { Spacer(Modifier.size(115.dp)) }
    }
}

@Composable
fun GalleryDropMenu(
    state: Boolean,
    menuList: List<String>?,
    callback: GalleryCallback?,
) {
    MaterialTheme(
        colorScheme = colorScheme.switch()
            .copy(
                surface = colorScheme.primaryContainer,
                onSurface = colorScheme.primary
            )
    ) {
        Box(
            Modifier.padding(
                start = 130.dp,
                top = 90.dp
            )
        ) {
            DropdownMenu(
                expanded = state,
                onDismissRequest = {
                    callback?.onMenuDismiss(false)
                },
                modifier = Modifier.background(
                    colorScheme.primaryContainer
                )
            ) {
                menuList?.forEachIndexed { i, it ->
                    DropdownMenuItem({
                        Text(
                            text = it,
                            color = colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                    }, {
                        callback?.onMenuDismiss(true)
                        callback?.onMenuItemClick(i)
                    })
                }
            }
        }
    }
}