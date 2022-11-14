package ru.rikmasters.gilty.login.presentation.ui.profile

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.GalleryCallback
import ru.rikmasters.gilty.shared.common.GalleryContent
import ru.rikmasters.gilty.shared.common.GalleryState
import ru.rikmasters.gilty.shared.shared.GradientButton
import java.io.File

var Avatar: String = ""
var Hidden: String = ""
var UserName: String = ""
var UserDescription: String = ""
var ListHidden: List<File> = listOf()

@Composable
fun ProfileSelectPhotoScreen(multiple: Boolean = false, nav: NavState = get()) {
    val context = LocalContext.current
    val commonPath = Environment.getExternalStorageDirectory().toString()
    val menuPoint = remember { mutableStateOf("$commonPath/DCIM/Camera") }
    val allDirectories = arrayListOf<File>()
    val selectImages = remember { mutableStateListOf<File>() }
    File("$commonPath/Pictures").listFiles()?.forEach { allDirectories.add(it) }
//    allDirectories.add(File("$commonPath/DCIM/Camera"))
//    allDirectories.add(File("$commonPath/DCIM/Screenshots"))
    val menuItems = arrayListOf<String>(); menuItems.add("Все медиа")
    allDirectories.forEach { if ((it.list()?.size ?: 0) != 0) menuItems.add(it.absolutePath) }
    val menuExpanded = remember { mutableStateOf(false) }
    val state = GalleryState(
        selectImages, multiple, menuExpanded.value,
        menuItems, menuPoint.value, allDirectories
    )
    GalleryContent(state, Modifier, object : GalleryCallback {
        override fun menu(state: Boolean) {
            menuExpanded.value = !menuExpanded.value
        }

        override fun onBack() {
            if (multiple) nav.navigateAbsolute("registration/hidden")
            else nav.navigateAbsolute("registration/profile")

        }

        override fun menuItemClick(item: String) {
            menuPoint.value = item
        }

        override fun onKebabClick() {
            Toast.makeText(
                context, "В меню пока пусто",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onImageSelect(file: File) {
            ListHidden = selectImages
            if (selectImages.contains(file))
                selectImages.remove(file)
            else selectImages.add(file)
        }

        override fun changeImage(file: File) {
            Avatar = file.toString()
            nav.navigateAbsolute("registration/resize?photo=$file")
        }
    })
    if (multiple) Box(Modifier.fillMaxSize()) {
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            "Прикрепить"
        ) { nav.navigateAbsolute("registration/hidden") }
    }
}