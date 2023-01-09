package ru.rikmasters.gilty.profile.presentation.ui.organizer.photo

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.PhotoView
import ru.rikmasters.gilty.shared.common.PhotoViewCallback
import ru.rikmasters.gilty.shared.common.PhotoViewState
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

@Composable
fun AvatarScreen(nav: NavState = get()) {
    val profile = DemoProfileModel
    var menuState by remember { mutableStateOf(false) }
    PhotoView(
        PhotoViewState(
            profile.avatar.id, ("1/1"), menuState, (0)
        ), Modifier, object: PhotoViewCallback {
            
            override fun onMenuItemClick(point: Int) {
                menuState = false
                nav.navigateAbsolute(
                    "registration/gallery?multi=false"
                )
            }
            
            override fun onMenuClick(state: Boolean) {
                menuState = state
            }
            
            override fun onBack() {
                nav.navigate("main")
            }
        })
}