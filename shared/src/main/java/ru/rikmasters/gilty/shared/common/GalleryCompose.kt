package ru.rikmasters.gilty.shared.common

//import android.graphics.BitmapFactory
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import ru.rikmasters.gilty.shared.shared.CheckBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.RowActionBar
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

//import java.io.File

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    GiltyTheme {
        GalleryContent(
            GalleryState(
                (true), listOf(),
            )
        )
    }
}

data class GalleryState(
//    val selectedImages: List<File>,
//    val multipleSelect: Boolean = false,
    val menuExpanded: Boolean = false,
    val menuItemList: List<String>,
//    val menuPoint: String,
//    val directories: List<File>
)

interface GalleryCallback : NavigationInterface {
    fun onKebabClick() {}
    fun menu(state: Boolean) {}
    fun menuItemClick(item: String) {}
//    fun onImageSelect(file: File) {}
//    fun changeImage(file: File) {}
}

@Composable
fun GalleryContent(
    state: GalleryState,
    modifier: Modifier = Modifier,
    callback: GalleryCallback? = null
) {
    Box(Modifier.padding(start = 130.dp, top = 90.dp)) {
        DropdownMenu(
            state.menuExpanded,
            { callback?.menu(false) },
            Modifier.background(colorScheme.primaryContainer),
        ) {
            state.menuItemList.forEach {
                DropdownMenuItem({
                    Text(
                        /*File(it).name*/it, Modifier,
                        colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium
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
                RowActionBar("Галерея")
                { callback?.onBack() }
                IconButton({ callback?.menu(true) }, Modifier.size(30.dp)) {
                    Icon(
                        if (state.menuExpanded) Filled.KeyboardArrowUp
                        else Filled.KeyboardArrowDown,
                        (null), Modifier.fillMaxSize(),
                        colorScheme.tertiary
                    )
                }
            }
            IconButton({ callback?.onKebabClick() }) {
                Icon(
                    painterResource(R.drawable.ic_kebab),
                    null, Modifier,
                    colorScheme.tertiary
                )
            }
        }
//        Grid(state, callback)
    }
}

//@Composable
//private fun Grid(state: GalleryState, callback: GalleryCallback?) {
//    LazyVerticalGrid(
//        GridCells.Fixed(3), Modifier.padding(horizontal = 4.dp),
//        verticalArrangement = Arrangement.spacedBy(4.dp),
//        horizontalArrangement = Arrangement.spacedBy(4.dp)
//    ) {
//        if (state.menuPoint == "Все медиа") {
//            state.directories.forEach { pack ->
//                pack.listFiles()?.let { list ->
//                    items(list, { it.name }) {
//                        ImageItem(
//                            it, state.multipleSelect,
//                            state.selectedImages.contains(it)
//                        ) { file ->
//                            if (state.multipleSelect) callback
//                                ?.onImageSelect(it)
//                            else callback?.changeImage(file)
//                        }
//                    }
//                }
//            }
//        } else {
//            File(state.menuPoint).listFiles()?.let { list ->
//                items(list, { it.name }) {
//                    ImageItem(
//                        it, state.multipleSelect,
//                        state.selectedImages.contains(it)
//                    ) { file ->
//                        if (state.multipleSelect) callback
//                            ?.onImageSelect(it)
//                        else callback?.changeImage(file)
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//private fun ImageItem(
//    file: File,
//    multipleSelect: Boolean,
//    selected: Boolean,
//    modifier: Modifier = Modifier,
//    onSelect: (File) -> Unit
//) {
//    val image =
//        BitmapFactory.decodeFile(file.canonicalPath)
//    Box(
//        modifier
//            .size(115.dp)
//            .clickable { onSelect(file) }) {
//        Image(
//            image.asImageBitmap(), (null),
//            Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop,
//        )
//        if (multipleSelect)
//            CheckBox(
//                selected,
//                Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(8.dp)
//            ) { onSelect(file) }
//    }
//}