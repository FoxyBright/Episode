package ru.rikmasters.gilty.login.presentation.ui.hidden

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
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.login.viewmodel.HiddenViewModel
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

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
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val photoViewType by vm.viewerType.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    val isDragging by vm.isDragging.collectAsState()

    LaunchedEffect(Unit) { vm.getHidden() }

    LaunchedEffect(key1 = isDragging, block = {
        if(!isDragging){
            vm.movePhotoRemote()
        }
    })
    
    HiddenContent(
        state = HiddenState(photoList = photoList,
            photosAmount = photoList.size//photosAmount
            ,
            photoViewState = photoViewState,
            viewerImages = photoList,
            viewerSelectImage = viewerSelectImage,
            viewerMenuState = false,
            viewerType = photoViewType),
        Modifier, object: HiddenCallback {

            override fun onSelectImage(image: AvatarModel) {
                scope.launch {
                    vm.changePhotoViewType(PhotoViewType.PHOTO)
                    vm.setPhotoViewSelected(image)
                    vm.changePhotoViewState(true)
                }
            }
            
            override fun onDeleteImage(image: AvatarModel) {
                scope.launch { vm.deleteImage(image) }
            }
            
            override fun openGallery() {
                context.checkStoragePermission(
                    storagePermissions, scope, asm,
                ) { nav.navigate("gallery?multi=true") }
            }

            override fun onPhotoViewDismiss(state: Boolean) {
                scope.launch { vm.changePhotoViewState(state) }
            }

            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                scope.launch {
                    // TODO СДЕЛАТЬ НОРМАЛЬНЫЙ МЕХАНИЗМ ДОБАВЛЕНИЯ И УДАЛЕНИЯ СКРЫТЫХ
                    nav.navigationBack()
                }
            }

            override fun onPhotoMoved(from: ItemPosition, to: ItemPosition) {
                scope.launch { vm.movePhoto(from, to) }
            }

            override fun onIsDraggingChange(value: Boolean) {
                scope.launch { vm.onIsDraggingChange(value) }
            }
        }
    )
}
