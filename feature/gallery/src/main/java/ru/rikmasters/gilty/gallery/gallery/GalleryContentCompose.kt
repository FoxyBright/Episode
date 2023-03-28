package ru.rikmasters.gilty.gallery.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.RowActionBar

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
    
    fun onBack() {}
    fun onAttach() {}
    fun onKebabClick() {}
    fun onMenuDismiss(state: Boolean) {}
    fun onMenuItemClick(item: Int) {}
    fun onImageClick(image: String) {}
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
            RowActionBar(stringResource(R.string.profile_gallery_title))
            { callback?.onBack() }
            IconButton(
                { callback?.onMenuDismiss(true) },
                Modifier.size(30.dp)
            ) {
                Icon(
                    if(menuState) Filled.KeyboardArrowUp
                    else Filled.KeyboardArrowDown,
                    (null), Modifier.fillMaxSize(),
                    colorScheme.tertiary
                )
            }
        }
        IconButton({ callback?.onKebabClick() }) {
            Icon(
                painterResource(R.drawable.ic_kebab),
                (null), Modifier,
                colorScheme.tertiary
            )
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
    val cells = 3
    LazyVerticalGrid(
        Fixed(cells),
        modifier.padding(horizontal = 4.dp),
        verticalArrangement = spacedBy(4.dp),
        horizontalArrangement = spacedBy(4.dp)
    ) {
        itemsIndexed(
            images,
            { i, _ -> i },
            contentType = { i, _ -> i }
        ) { _, image ->
            GalleryImage(
                image, type,
                selected?.contains(image),
                Modifier, isWeb, callback
            )
        }
        items(cells) { Spacer(Modifier.size(115.dp)) }
    }
}

@Composable
fun GalleryDropMenu(
    state: Boolean,
    menuList: List<String>?,
    callback: GalleryCallback?,
) {
    Box(Modifier.padding(start = 130.dp, top = 90.dp)) {
        DropdownMenu(
            state, { callback?.onMenuDismiss(false) },
            Modifier.background(colorScheme.primaryContainer),
        ) {
            menuList?.forEachIndexed { i, it ->
                DropdownMenuItem({
                    Text(
                        it, Modifier,
                        colorScheme.tertiary,
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

@Composable
fun StoragePermissionBs(
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .padding(16.dp), SpaceBetween
    ) {
        Text(
            stringResource(R.string.profile_gallery_title),
            Modifier, style = typography.labelLarge,
        )
        Column(
            Modifier.fillMaxWidth(),
            Top, CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.ic_image_box),
                (null), Modifier.size(50.dp),
                colorScheme.primary
            )
            Text(
                stringResource(R.string.permissions_settings_label),
                Modifier.padding(top = 22.dp),
                style = typography.labelLarge,
                textAlign = Center
            )
        }
        GradientButton(
            Modifier.padding(bottom = 40.dp),
            stringResource(R.string.permissions_settings_button)
        ) { onSettingsClick?.let { it() } }
    }
}