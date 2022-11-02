package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import java.io.File

@Composable
fun HiddenPhotoScreen(photos: List<File> = listOf(), nav: NavState = get()) {
    val photoList = remember { mutableStateListOf<File>() }
    photos.forEach { photoList.add(it) }
    ListHidden.forEach { photoList.add(it) } // временная замена локального хранилища
    HiddenPhotoContent(HiddenPhotoState(photoList), Modifier, object : HiddenPhotoCallback {

        override fun onDeleteImage(file: File) {
            ListHidden = photoList
            photoList.remove(file)
        }

        override fun openGallery() {
            nav.navigateAbsolute("registration/gallery?multi=true")
        }

        override fun onBack() {
            nav.navigateAbsolute("registration/profile")
        }

        override fun onNext() {
            Hidden = if (photoList.isEmpty()) "" else photoList.last().toString()
            nav.navigateAbsolute(
                "registration/profile?hp=${
                    if (photoList.isEmpty()) "" else photoList.last()
                }"
            )
        }
    })
}
