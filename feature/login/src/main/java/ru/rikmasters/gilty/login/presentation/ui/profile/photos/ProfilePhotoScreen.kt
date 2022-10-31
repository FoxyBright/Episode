package ru.rikmasters.gilty.login.presentation.ui.profile.photos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.GalleryCallback
import ru.rikmasters.gilty.shared.common.GalleryContent
import ru.rikmasters.gilty.shared.common.GalleryState
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel

@Composable
fun ProfileSelectPhotoScreen(nav: NavState = get()) {
    val menuItems = listOf(
        "Все медиа", "Все видео",
        "WhatsApp images", "Screenshots",
        "Viber", "Telegram", "Camera", "Instagram"
    )
    val imageList =
        arrayListOf<ImageModel>()
    val menuExpanded =
        remember { mutableStateOf(false) }
    repeat(22) { imageList.add(DemoAvatarModel) }
    val state = GalleryState(
        imageList, listOf(),
        false,
        menuExpanded.value, menuItems
    )
    GalleryContent(state, Modifier, object : GalleryCallback {
        override fun menu(state: Boolean) {
            menuExpanded.value = !menuExpanded.value
        }

        override fun onBack() {
            nav.navigateAbsolute("registration/profile")
        }

        override fun changeImage(index: Int) {
            nav.navigateAbsolute("registration/resize?photo=${imageList[index].id}")
        }
    })
}