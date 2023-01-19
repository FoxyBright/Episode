package ru.rikmasters.gilty.profile.presentation.ui.user.gallerey

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.auth.profile.Image
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.HiddenBsViewModel

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
            
            override fun onSelectImage(image: Image) {
                scope.launch {
                    asm.bottomSheet.collapsed()
                    nav.navigate("avatar?image=${image.thumbnail.url}")
                }
            }
            
            override fun onDeleteImage(image: Image) {
                scope.launch { vm.deleteImage(image) }
            }
            
            override fun openGallery() {
                scope.launch {
                    asm.bottomSheet.collapsed()
                    nav.navigate("gallery?multi=true")
                }
            }
        })
}
