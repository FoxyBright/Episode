package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.gallery.*
import ru.rikmasters.gilty.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.shared.R

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun GalleryBs(
    vm: GalleryViewModel,
    isOnline: Boolean,
    chatId: String,
) {
    
    val permission = rememberPermissionState(
        if(SDK_INT < TIRAMISU)
            READ_EXTERNAL_STORAGE
        else READ_MEDIA_IMAGES
    )
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val selected by vm.selected.collectAsState()
    val images by vm.images.collectAsState()
    
    LaunchedEffect(Unit) { vm.clearSelect() }
    
    LaunchedEffect(permission.hasPermission) {
        if(permission.hasPermission) vm.getImages()
    }
    
    if(!permission.hasPermission) {
        StoragePermissionBs {
            scope.launch {
                permission.launchPermissionRequest()
                if(permission.hasPermission)
                    vm.getImages()
            }
        }
    } else {
        GalleryBsContent(
            GalleryState(
                MULTIPLE, images,
                selected, (false),
                (null), isOnline,
                stringResource(R.string.send_button),
                selected.isNotEmpty()
            ), Modifier, object: GalleryCallback {
                
                override fun onAttach() {
                    scope.launch {
                        asm.bottomSheet.collapse()
                        vm.sendImages(chatId)
                    }
                }
                
                override fun onImageClick(image: String) {
                    scope.launch { vm.selectImage(image) }
                }
            }
        )
    }
}