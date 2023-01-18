package ru.rikmasters.gilty.profile.presentation.ui.organizer.photo

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.AvatarViewModel
import ru.rikmasters.gilty.shared.common.PhotoView
import ru.rikmasters.gilty.shared.common.PhotoViewCallback
import ru.rikmasters.gilty.shared.common.PhotoViewState

@Composable
fun AvatarScreen(
    vm: AvatarViewModel,
    avatar: String
) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val menu by vm.menu.collectAsState()
    
    PhotoView(
        PhotoViewState(
            avatar, ("1/1"), menu, (0)
        ), Modifier, object: PhotoViewCallback {
            
            override fun onMenuItemClick(point: Int) {
                scope.launch {
                    vm.showMenu(false)
                    nav.navigateAbsolute(
                        "registration/gallery?multi=false"
                    )
                }
            }
            
            override fun onMenuClick(state: Boolean) {
                scope.launch { vm.showMenu(state) }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
        })
}