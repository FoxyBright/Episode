package ru.rikmasters.gilty.profile.presentation.ui.photo.gallerey

import android.graphics.Rect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.canhub.cropper.*
import com.canhub.cropper.CropImageView.CropCornerShape.OVAL
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.permission.PermissionUtils.requestPermissions
import ru.rikmasters.gilty.profile.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.shared.common.*
import java.io.File

@Composable
fun ProfileSelectPhotoScreen(
    vm: GalleryViewModel,
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
                    val image = result.originalUri?.toFile()?.path ?: ""
                    val rect = result.cropRect ?: Rect()
                    vm.imageClick(
                        image, listOf(
                            (rect.width() - rect.centerX() * 2),
                            (rect.height() - rect.centerY() * 2),
                            rect.width(), rect.height()
                        )
                    )
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
                            aspectRatioX = 2,
                            aspectRatioY = 3,
                            guidelinesColor = Transparent.toArgb(),
                            cropCornerRadius = 0f,
                            cornerShape = OVAL,
                            cropShape = CropImageView.CropShape.RECTANGLE,
                            scaleType = CropImageView.ScaleType.CENTER_CROP,
                            progressBarColor = Color(0xFFFF4745).toArgb(),
                            fixAspectRatio = true,
                            activityBackgroundColor = Black.toArgb()
                        )
                    )
                )
            }
        })
}