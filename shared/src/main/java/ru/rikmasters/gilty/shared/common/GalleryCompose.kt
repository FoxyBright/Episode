package ru.rikmasters.gilty.shared.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel
import ru.rikmasters.gilty.shared.shared.CheckBox
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import java.io.File

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    GiltyTheme {
        val menuItems = listOf(
            "Все медиа",
            "Все видео",
            "WhatsApp images",
            "Screenshots",
            "Viber",
            "Telegram",
            "Camera",
            "Instagram"
        )
        val imageList = arrayListOf<ImageModel>()
        val menuExpanded = remember { mutableStateOf(false) }
        repeat(22) { imageList.add(DemoAvatarModel) }
        val selImageList =
            remember { mutableStateListOf<Int>() }
        val state = GalleryState(
            imageList, selImageList,
            true, menuExpanded.value, menuItems
        )
        GalleryContent(state, Modifier, object : GalleryCallback {
            override fun menu(state: Boolean) {
                menuExpanded.value = !menuExpanded.value
            }

            override fun onImageSelect(index: Int) {
                if (selImageList.contains(index))
                    selImageList.remove(index)
                else selImageList.add(index)
            }
        })
    }
}

data class GalleryState(
    val images: List<ImageModel>,
    val selectedImages: List<Int>,
    val multipleSelect: Boolean = false,
    val menuExpanded: Boolean = false,
    val menuItemList: List<String>
)

interface GalleryCallback : NavigationInterface {
    fun onKebabClick() {}
    fun menu(state: Boolean) {}
    fun menuItemClick(item: String) {}
    fun onImageSelect(index: Int) {}
    fun changeImage(index: Int) {}
}

@Composable
fun GalleryContent(
    state: GalleryState,
    modifier: Modifier = Modifier,
    callback: GalleryCallback? = null
) {
    val commonPath = Environment.getExternalStorageDirectory().toString()
    val allDirectories = arrayListOf<File>()
    File("$commonPath/Pictures").listFiles()?.forEach { allDirectories.add(it) }
    allDirectories.add(File("$commonPath/DCIM/Camera"))
    allDirectories.add(File("$commonPath/DCIM/Screenshots"))
    val imageList = arrayListOf<File>()
    allDirectories.forEach { it.listFiles()?.forEach { image -> imageList.add(image) } }
    Box(Modifier.padding(start = 130.dp, top = 90.dp)) {
        DropdownMenu(
            state.menuExpanded,
            { callback?.menu(false) },
            Modifier.background(ThemeExtra.colors.cardBackground),
        ) {
            state.menuItemList.forEach {
                DropdownMenuItem({
                    Text(
                        it, Modifier, ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.Body1Medium
                    )
                }, {
                    callback?.menu(true)
                    callback?.menuItemClick(it)
                })
            }
        }
    }
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 26.dp),
            Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RowActionBar(
                    "Галерея",
                    modifier = Modifier.fillMaxWidth(0.38f)
                )
                { callback?.onBack() }
                IconButton({ callback?.menu(true) }, Modifier.size(30.dp)) {
                    Icon(
                        if (state.menuExpanded)
                            Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                        null, Modifier.fillMaxSize()
                    )
                }
            }
            IconButton({ callback?.onKebabClick() }) {
                Icon(
                    painterResource(R.drawable.ic_kebab),
                    null, Modifier,
                    ThemeExtra.colors.mainTextColor
                )
            }
        }
        LazyVerticalGrid(
            GridCells.Fixed(3), Modifier.padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(imageList) {
                val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                ImageItem(
                    bitmap, state.multipleSelect,
                    /*state.selectedImages.contains(index)*/false
                ) {
                    if (state.multipleSelect) callback?.onImageSelect(1)
                    else callback?.changeImage(1)
                }
            }
        }
    }
}

@Composable
private fun ImageItem(
    image: Bitmap,
    multipleSelect: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    Box(
        modifier
            .size(115.dp)
            .clickable { onSelect() }) {
        Image(
            image.asImageBitmap(),
            (null), Modifier.fillMaxSize(),
            Alignment.Center, ContentScale.Crop,
        )
        if (multipleSelect)
            CheckBox(
                selected,
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) { onSelect() }
    }
}