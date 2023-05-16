package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.gallery.checkStoragePermission
import ru.rikmasters.gilty.gallery.permissionState
import ru.rikmasters.gilty.profile.viewmodel.bottoms.HiddenViewModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenBsScreen(vm: HiddenViewModel) {
    
    val photoList = vm.images.collectAsLazyPagingItems()
    val photoAmount = vm.photosAmount.collectAsState()
    val storagePermissions = permissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
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
                    context.checkStoragePermission(
                        storagePermissions, scope, asm,
                    ) { nav.navigate("gallery?multi=true") }
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
