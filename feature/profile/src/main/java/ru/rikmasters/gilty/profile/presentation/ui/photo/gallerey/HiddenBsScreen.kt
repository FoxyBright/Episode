package ru.rikmasters.gilty.profile.presentation.ui.photo.gallerey

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.bottoms.HiddenBsViewModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@Composable
fun HiddenBsScreen(vm: HiddenBsViewModel) {
    
    val nav = get<NavState>()
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    val photoList by vm.photoList.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.uploadPhotoList()
    }
    HiddenBsContent(
        photoList, Modifier,
        object: HiddenBsCallback {
            
            override fun onSelectImage(image: AvatarModel) {
                scope.launch {
                    asm.bottomSheet.collapse()
                    nav.navigate("avatar?type=0&image=${image.url}")
                }
            }
            
            override fun onDeleteImage(image: AvatarModel) {
                scope.launch { vm.deleteImage(image) }
            }
            
            override fun openGallery() {
                scope.launch {
                    asm.bottomSheet.collapse()
                    nav.navigate("gallery?multi=true")
                }
            }
        })
}
