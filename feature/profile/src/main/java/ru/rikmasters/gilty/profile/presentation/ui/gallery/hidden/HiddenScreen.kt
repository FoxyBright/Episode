package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.bottoms.HiddenViewModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@Composable
fun HiddenBsScreen(vm: HiddenViewModel) {
    
    val photoList = vm.images.collectAsLazyPagingItems()
    val photoAmount = vm.photosAmount.collectAsState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()

    LaunchedEffect(Unit) {
        vm.uploadPhotoList()
        vm.getHiddenPhotosAmount()
    }

    HiddenBsContent(
        HiddenBsState(photoList,photoAmount.value), Modifier, object : HiddenBsCallback {
            
            override fun onSelectImage(image: AvatarModel) {
                scope.launch {
                    asm.bottomSheet.collapse()
                    nav.navigate("avatar?type=0&image=${image.url}")
                }
            }

            override fun openGallery() {
                scope.launch {
                    asm.bottomSheet.collapse()
                    nav.navigate("gallery?multi=true")
                }
            }

            override fun onDeleteImage(image: AvatarModel) {
                scope.launch { vm.deleteImage(image.id) }
            }

            override fun onBack() {
                nav.navigationBack()
            }
        }
    )
}
