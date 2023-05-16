package ru.rikmasters.gilty.login.presentation.ui.gallery

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.gallery.checkStoragePermission
import ru.rikmasters.gilty.gallery.permissionState
import ru.rikmasters.gilty.login.viewmodel.HiddenViewModel
import ru.rikmasters.gilty.shared.common.HiddenCallback
import ru.rikmasters.gilty.shared.common.HiddenContent
import ru.rikmasters.gilty.shared.common.HiddenState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenScreen(vm: HiddenViewModel) {
    
    val photoList by vm.photoList.collectAsState()
    val photosAmount by vm.photosAmount.collectAsState()
    val storagePermissions = permissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    LaunchedEffect(Unit) { vm.getHidden() }
    
    HiddenContent(
        HiddenState(photoList, photosAmount),
        Modifier, object: HiddenCallback {
            
            override fun onSelectImage(image: String) {
                scope.launch { vm.selectImage(image) }
            }
            
            override fun onDeleteImage(image: String) {
                scope.launch { vm.deleteImage(image) }
            }
            
            override fun openGallery() {
                context.checkStoragePermission(
                    storagePermissions, scope, asm,
                ) { nav.navigate("gallery?multi=true") }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                scope.launch {
                    // TODO СДЕЛАТЬ НОРМАЛЬНЫЙ МЕХАНИЗМ ДОБАВЛЕНИЯ И УДАЛЕНИЯ СКРЫТЫХ
                    nav.navigate("profile")
                }
            }
        }
    )
}
