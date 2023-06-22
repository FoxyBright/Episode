package ru.rikmasters.gilty.login.presentation.ui.gallery

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.StoragePermissionBs
import ru.rikmasters.gilty.gallery.gallery.*
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.SINGLE
import ru.rikmasters.gilty.login.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.shared.R

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun GalleryScreen(
    vm: GalleryViewModel,
    multiple: Boolean,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val permission = rememberPermissionState(
        if(SDK_INT < TIRAMISU) READ_EXTERNAL_STORAGE
        else READ_MEDIA_IMAGES
    )
    
    val type = if(multiple)
        MULTIPLE else SINGLE
    
    val images by vm.images.collectAsState()
    val selected by vm.selected.collectAsState()
    val menuState by vm.menuState.collectAsState()
    val filters by vm.filters.collectAsState()
    
    LaunchedEffect(permission.hasPermission) {
        permission.launchPermissionRequest()
        if(permission.hasPermission) {
            vm.updateImages()
            asm.bottomSheet.collapse()
        } else asm.bottomSheet.expand {
            StoragePermissionBs {
                scope.launch {
                    permission.launchPermissionRequest()
                }
            }
        }
    }
    
    val back = colorScheme.background
    LaunchedEffect(Unit) {
        asm.systemUi.setNavigationBarColor(back)
        asm.systemUi.setSystemBarsColor(back)
    }
    
    Use<GalleryViewModel>(LoadingTrait) {
        GalleryContent(
            state = GalleryState(
                type = type,
                images = images,
                selectedImages = selected,
                menuState = menuState,
                menuItems = filters,
                isOnline = (false),
                buttonLabel = stringResource(R.string.add_button),
                buttonEnabled = selected.isNotEmpty(),
            ), modifier = Modifier, callback = object: GalleryCallback {
                
                override fun onImageClick(image: String) {
                    scope.launch {
                        if(multiple) vm.selectImage(image)
                        else nav.navigate("cropper?image=$image")
                    }
                }
                
                override fun onMenuItemClick(item: Int) {
                    scope.launch {
                        vm.onMenuItemClick(
                            if(item == 0) "" else
                                filters[item]
                        )
                    }
                }
                
                override fun onMenuDismiss(state: Boolean) {
                    scope.launch { vm.menuDismiss(state) }
                }
                
                override fun onAttach() {
                    scope.launch {
                        vm.attach()
                        nav.navigationBack()
                    }
                }
                
                override fun onBack() {
                    nav.navigationBack()
                }
            }
        )
    }
}