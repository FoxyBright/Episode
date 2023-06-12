package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import androidx.compose.runtime.*
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
import ru.rikmasters.gilty.profile.viewmodel.bottoms.HiddenViewModel
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HiddenBsScreen(
    vm: HiddenViewModel,
    update: Boolean,
) {
    
    val photoList = vm.images.collectAsLazyPagingItems()
    val photoAmount = vm.photosAmount.collectAsState()
    val storagePermissions = permissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    val viewerSelectImage by vm.viewerSelectImage.collectAsState()
    val viewerImages by vm.photos.collectAsState()
    val photoViewType by vm.viewerType.collectAsState()
    val photoViewState by vm.viewerState.collectAsState()
    
    
    LaunchedEffect(Unit) {
        vm.uploadPhotoList(true)
    }

    LaunchedEffect(key1 = photoList.itemSnapshotList.items, block = {
        vm.setPhotoList(photoList.itemSnapshotList.items)
    })
    
    HiddenBsContent(
        state = HiddenBsState(
            photoList = photoList,
            photoAmount = photoAmount.value,
            photoViewState = photoViewState,
            viewerImages = viewerImages,
            viewerSelectImage = viewerSelectImage,
            viewerMenuState = false,
            viewerType = photoViewType
        ),
        callback = object: HiddenBsCallback {
            
            override fun onSelectImage(image: AvatarModel) {
                scope.launch {
                    vm.changePhotoViewType(PhotoViewType.PHOTO)
                    vm.setPhotoViewSelected(image)
                    vm.setPhotoViewImages(photoList.itemSnapshotList.items)
                    vm.changePhotoViewState(true)
                }
            }

            override fun onPhotoMoved(from: ItemPosition, to: ItemPosition) {
                scope.launch {
                    vm.moveDog(from, to)
                }
            }
            override fun openGallery() {
                scope.launch {
                    context.checkStoragePermission(
                        storagePermissions, scope, asm,
                    ) {
                        nav.navigate(
                            "gallery?multi=true" +
                                    "&dest=profile/hidden?update=${true}"
                        )
                    }
                }
            }
            
            override fun onDeleteImage(image: AvatarModel) {
                scope.launch { vm.deleteImage(image.id) }
            }
            
            override fun onBack() {
                nav.navigate("main?update=$update")
            }
            
            override fun onPhotoViewDismiss(state: Boolean) {
                scope.launch { vm.changePhotoViewState(state) }
            }
        }
    )
}
