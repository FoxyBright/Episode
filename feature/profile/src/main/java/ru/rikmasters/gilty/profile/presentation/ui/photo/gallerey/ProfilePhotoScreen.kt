package ru.rikmasters.gilty.profile.presentation.ui.photo.gallerey

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.shared.common.*
import ru.rikmasters.gilty.shared.common.extentions.Permissions.Companion.requestPermission

@Composable
fun ProfileSelectPhotoScreen(
    vm: GalleryViewModel,
    multiple: Boolean = false,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val permissions by vm.permissions.collectAsState()
    val images by vm.images.collectAsState()
    val selected by vm.selected.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val filters by vm.filters.collectAsState()
    
    LaunchedEffect(Unit) {
        if(!permissions)
            asm.bottomSheet.expand {
                StoragePermissionBs {
                    launch {
                        requestPermission(context)
                        if(vm.setPermissions())
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
            }
        }
    )
}