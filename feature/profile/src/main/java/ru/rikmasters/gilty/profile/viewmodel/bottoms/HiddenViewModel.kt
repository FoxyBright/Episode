package ru.rikmasters.gilty.profile.viewmodel.bottoms

import android.content.Context
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class HiddenViewModel : ViewModel() {

    private val profileManager by inject<ProfileManager>()

    private val context = getKoin().get<Context>()

    private val refresh = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val images by lazy {
        combine(
            refresh
        ) { it }.flatMapLatest {
            this.getHiddenPhotosAmount()
            profileManager.getHiddenPhotos()
        }.cachedIn(coroutineScope)

    }

    private val _photosAmount = MutableStateFlow(0)
    val photosAmount = _photosAmount.asStateFlow()

    private val _photos = MutableStateFlow(listOf<AvatarModel>())
    val photos = _photos.asStateFlow()

    private val _viewerState = MutableStateFlow(false)
    val viewerState = _viewerState.asStateFlow()

    private val _viewerType = MutableStateFlow(PhotoViewType.PHOTO)
    val viewerType = _viewerType.asStateFlow()

    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()

    suspend fun movePhoto(fromOr: ItemPosition, toOr: ItemPosition) {
        (fromOr.index - 1).let { from ->
            (toOr.index - 1).let { to ->
                if (_photos.value.size > from && _photos.value.size > to) {
                    val fromId = _photos.value[from].id
                    val newList = _photos.value.toMutableList().apply {
                        add(to, removeAt(from))
                    }
                    _photos.emit(newList)
                    profileManager.changeAlbumPosition(fromId, to + 1).on(
                        success = {},
                        loading = {},
                        error = {}
                    )
                }
            }
        }
    }

    suspend fun setPhotoList(value: List<AvatarModel>) {
        _photos.emit(value)
    }

    //fun isDogDragEnabled(draggedOver: ItemPosition, dragging: ItemPosition) = dogs.getOrNull(draggedOver.index)?.isLocked != true

    suspend fun uploadPhotoList(forceWeb: Boolean) {
        profileManager.getProfileHiddens(forceWeb)
        refreshImages()
    }

    suspend fun deleteImage(imageId: String) {
        profileManager.deleteHidden(imageId).on(
            success = {
                profileManager.getProfile(true)
                refreshImages()
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    suspend fun getHiddenPhotosAmount() {
        _photosAmount.emit(
            profileManager.getHiddenPhotosAmount()
        )
    }

    suspend fun changePhotoViewState(state: Boolean) {
        _viewerState.emit(state)
    }

    suspend fun changePhotoViewType(type: PhotoViewType) {
        _viewerType.emit(type)
    }

    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }

    private suspend fun refreshImages() {
        refresh.emit(!refresh.value)
    }
}
