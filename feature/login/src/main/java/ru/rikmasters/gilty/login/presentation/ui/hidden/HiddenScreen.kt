package ru.rikmasters.gilty.login.presentation.ui.hidden

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
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.login.viewmodel.HiddenViewModel
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenScreen(vm: HiddenViewModel) {

    val photoList = vm.images.collectAsLazyPagingItems()
    val photoAmount by vm.photosAmount.collectAsState()
    val storagePermissions = permissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val photoViewType by vm.viewerType.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    val isDragging by vm.isDragging.collectAsState()
    val viewerImages by vm.photos.collectAsState()
    val alert by vm.alert.collectAsState()


    LaunchedEffect(Unit) { vm.refreshImages() }

    LaunchedEffect(key1 = isDragging, block = {
        if(!isDragging){
            vm.movePhotoRemote()
        }
    })

    LaunchedEffect(key1 = photoList.itemSnapshotList.items, block = {
        vm.setPhotoList(photoList.itemSnapshotList.items)
        vm.getHiddenPhotosAmount()

    })
    
    HiddenContent(
        state = HiddenState(
            photoList = photoList,
            photosAmount = photoAmount,
            photoViewState = photoViewState,
            viewerImages = viewerImages,
            viewerSelectImage = viewerSelectImage,
            viewerMenuState = false,
            viewerType = photoViewType,
            alert = alert,
        ),
        modifier = Modifier, object: HiddenCallback {

            override fun onSelectImage(image: AvatarModel) {
                scope.launch {
                    vm.changePhotoViewType(PhotoViewType.PHOTOS)
                    vm.setPhotoViewSelected(image)
                    vm.changePhotoViewState(true)
                }
            }

            override fun onDeleteImage(image: AvatarModel) {
                scope.launch {
                    vm.alertDismiss(true)
                    vm.setSelectedImageId(image.id)
                }
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
            override fun closeAlert(delete:Boolean) {
                scope.launch {
                    vm.alertDismiss(false)
                    if(delete){
                        vm.deleteImage()
                    }
                }
            }
        }
    )
}
