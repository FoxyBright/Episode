package ru.rikmasters.gilty.profile.viewmodel.bottoms

import android.content.Context
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class HiddenViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val context = getKoin().get<Context>()
    
    val images by lazy {
        profileManager.getHiddenPhotos().cachedIn(coroutineScope)
    }
    
    private val _photosAmount = MutableStateFlow(0)
    val photosAmount = _photosAmount.asStateFlow()

    private val _viewerState = MutableStateFlow(false)
    val viewerState = _viewerState.asStateFlow()

    private val _viewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages = _viewerImages.asStateFlow()

    private val _viewerType = MutableStateFlow(PhotoViewType.PHOTO)
    val viewerType = _viewerType.asStateFlow()

    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()

    suspend fun uploadPhotoList(forceWeb: Boolean = false) {
        profileManager.getProfileHiddens(forceWeb)
    }
    
    suspend fun deleteImage(imageId: String) {
        profileManager.deleteHidden(imageId).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
        profileManager.getProfile(true)
    }
    
    suspend fun getHiddenPhotosAmount() {
        _photosAmount.emit(profileManager.getHiddenPhotosAmount())
    }
    suspend fun changePhotoViewState(state: Boolean) {
        _viewerState.emit(state)
    }

    suspend fun changePhotoViewType(type: PhotoViewType) {
        _viewerType.emit(type)
    }

    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _viewerImages.emit(list)
    }

    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
}
