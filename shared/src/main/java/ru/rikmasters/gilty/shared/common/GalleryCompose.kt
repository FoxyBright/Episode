package ru.rikmasters.gilty.shared.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment.MEDIA_MOUNTED
import android.os.Environment.getExternalStorageState
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.Images.Media.DATA
import android.provider.MediaStore.Images.Media._ID
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.CheckBox
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import java.io.File

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    GiltyTheme {
        GalleryContent(
            GalleryState()
        )
    }
}

data class GalleryState(
    val menuItems: List<String>? = null,
    val multiple: Boolean = false,
    val selectedImages: List<String>? = null,
    val menuState: Boolean = false,
    val images: MutableList<String>? = null
)

interface GalleryCallback {
    
    fun onBack()
    fun onAttach() {}
    fun onKebabClick() {}
    fun onMenuDismiss(state: Boolean) {}
    fun onMenuItemClick(item: String) {}
    fun onImageSelect(image: String) {}
    fun onImageClick(image: String) {}
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GalleryContent(
    state: GalleryState,
    modifier: Modifier = Modifier,
    callback: GalleryCallback? = null
) {
    Scaffold(modifier, {
        DropMenu(
            state.menuState,
            state.menuItems,
            { callback?.onMenuDismiss(it) }
        ) { callback?.onImageSelect(it) }
        TopBar(state.menuState, callback)
    }) { paddings ->
        state.images?.let {
            Grid(
                modifier.padding(top = paddings.calculateTopPadding()),
                it, state.selectedImages,
                state.multiple,
                callback
            )
        }
    }
    if(state.multiple) Box(Modifier.fillMaxSize()) {
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.profile_gallery_attach_button)
        ) { callback?.onAttach() }
    }
}

@Composable
private fun TopBar(
    menuState: Boolean,
    callback: GalleryCallback?
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 26.dp),
        SpaceBetween
    ) {
        Row(Modifier, Start, CenterVertically) {
            RowActionBar(
                stringResource(R.string.profile_gallery_title)
            ) { callback?.onBack() }
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
private fun Grid(
    modifier: Modifier = Modifier,
    images: MutableList<String>,
    selected: List<String>?,
    multiple: Boolean,
    callback: GalleryCallback?
) {
    val cells = 3
    LazyVerticalGrid(
        Fixed(cells),
        modifier.padding(horizontal = 4.dp),
        verticalArrangement = spacedBy(4.dp),
        horizontalArrangement = spacedBy(4.dp)
    ) {
        items(
            images.size, { it },
            contentType = { it }
        ) {
            val file = images[it]
            ImageItem(
                file, multiple,
                (selected?.contains(file) == true)
            ) {
                if(multiple)
                    callback?.onImageSelect(file)
                else
                    callback?.onImageClick(file)
            }
        }
        items(cells) { Spacer(Modifier.size(115.dp)) }
    }
}

@Composable
private fun DropMenu(
    state: Boolean,
    menuList: List<String>?,
    onDismiss: (Boolean) -> Unit,
    onItemSelect: (String) -> Unit,
) {
    Box(Modifier.padding(start = 130.dp, top = 90.dp)) {
        DropdownMenu(
            state, { onDismiss(false) },
            Modifier.background(
                colorScheme.primaryContainer
            ),
        ) {
            menuList?.let { list ->
                list.forEach {
                    DropdownMenuItem({
                        Text(
                            it, Modifier,
                            colorScheme.tertiary,
                            style = typography.bodyMedium
                        )
                    }, {
                        onDismiss(true)
                        onItemSelect(it)
                    })
                }
            }
        }
    }
}

@Composable
private fun ImageItem(
    image: String,
    multiple: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    log.d(image)
    Box(
        modifier
            .size(115.dp)
            .clickable { onClick() }) {
        Image(
            rememberAsyncImagePainter(File(image)),
            (null), Modifier.fillMaxSize(),
            contentScale = Crop,
        )
        if(multiple) CheckBox(
            selected, Modifier
                .align(TopEnd)
                .padding(8.dp)
        ) { onClick() }
    }
}

@SuppressLint("Range")
fun getImages(
    context: Context,
    filter: String = ""
): MutableList<String> {
    val imgList: MutableList<String> = ArrayList()
    val storage = getExternalStorageState()
    if(storage == MEDIA_MOUNTED) {
        context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(DATA, _ID),
            (null), (null), _ID
        )?.let {
            for(i in 0 until it.count) {
                try {
                    it.moveToPosition(i * 3)
                    val file = it.getString(it.getColumnIndex(DATA))
                    if(file.contains("/$filter")) imgList.add(file)
                } catch(_: Exception) {
                    break
                }
            }
            it.close()
        }
    }
    return imgList
}