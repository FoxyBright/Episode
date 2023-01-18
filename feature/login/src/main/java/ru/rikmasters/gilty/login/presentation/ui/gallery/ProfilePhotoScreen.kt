package ru.rikmasters.gilty.login.presentation.ui.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.permission.PermissionUtils.requestPermissions
import ru.rikmasters.gilty.login.viewmodel.GalereyViewModel
import ru.rikmasters.gilty.shared.common.GalleryCallback
import ru.rikmasters.gilty.shared.common.GalleryContent
import ru.rikmasters.gilty.shared.common.GalleryState
import java.io.File

@Composable
fun ProfileSelectPhotoScreen(
    vm: GalereyViewModel,
    multiple: Boolean = false,
) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    
    val images by vm.images.collectAsState()
    val selected by vm.selected.collectAsState()
    val filters by vm.filters.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val permissions by vm.permissions.collectAsState()
    
    val resizeLauncher =
        rememberLauncherForActivityResult(CropImageContract()) { result ->
            if(result.isSuccessful) {
                scope.launch {
                    val image = result.getUriFilePath(context) ?: ""
                    
                    result.cropPoints.forEach {
                        log.d("array points --->>> $it")
                    }
                    vm.imageClick(image)
                    nav.navigationBack()
                }
            }
        }
    
    LaunchedEffect(Unit) {
        if(!permissions)
            asm.bottomSheet.expand {
                StoragePermissionBs {
                    requestPermissions(context)
                    launch {
                        vm.setPermissions(true)
                        asm.bottomSheet.collapse()
                    }
                }
            }
        else scope.launch { vm.updateImages() }
    }
    
    GalleryContent(
        GalleryState(
            filters, multiple,
            selected, menuState, images
        ), Modifier, object: GalleryCallback {
            
            override fun onAttach() {
                scope.launch {
                    vm.attach()
                    nav.navigationBack()
                }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onKebabClick() {
                scope.launch { vm.kebab() }
            }
            
            override fun onMenuDismiss(state: Boolean) {
                scope.launch { vm.menuDismiss(state) }
            }
            
            override fun onMenuItemClick(item: String) {
                scope.launch { vm.onMenuItemClick(item) }
            }
            
            override fun onImageSelect(image: String) {
                scope.launch { vm.selectImage(image) }
            }
            
            override fun onImageClick(image: String) {
                resizeLauncher.launch(
                    CropImageContractOptions(
                        File(image).toUri(),
                        CropImageOptions(
                            aspectRatioX = 200,
                            aspectRatioY = 100
                        )
                    )
                )
            }
        })
}