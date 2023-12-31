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

    private val _isDragging = MutableStateFlow(false)
    val isDragging = _isDragging.asStateFlow()

    private val _lastPositionsChanged = MutableStateFlow(Pair<String?, Int?>(null, null))

    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()

    private val _selectedImageId = MutableStateFlow<String?>(null)

    suspend fun getHiddenPhotosAmount() {
        _photosAmount.emit(
            profileManager.getHiddenPhotosAmount()
        )
    }

    suspend fun movePhoto(fromOr: ItemPosition, toOr: ItemPosition) {
        (fromOr.index - 1).let { from ->
            (toOr.index - 1).let { to ->
                if (_photos.value.size > from && _photos.value.size > to) {
                    val fromId = _photos.value[from].id
                    val newList = _photos.value.toMutableList().apply {
                        add(to, removeAt(from))
                    }
                    _photos.emit(newList)

                    var newLastChanged = Pair<String?, Int?>(null, null)
                    newLastChanged = if(_lastPositionsChanged.value.first == null){
                        newLastChanged.copy(first = fromId)
                    }else {
                        newLastChanged.copy(first = _lastPositionsChanged.value.first)
                    }
                    newLastChanged = newLastChanged.copy(second = to + 1)
                    _lastPositionsChanged.emit(newLastChanged)
                }
            }
        }
    }

    suspend fun setPhotoList(value: List<AvatarModel>) {
        _photos.emit(value)
    }

    suspend fun deleteImage() {
        _selectedImageId.value?.let { imageId ->
            profileManager.deleteHidden(imageId).on(
                success = {
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

    suspend fun refreshImages() {
        refresh.emit(!refresh.value)
    }

    suspend fun onIsDraggingChange(value: Boolean) {
        _isDragging.emit(value)
    }
    suspend fun movePhotoRemote() {
        val fromId = _lastPositionsChanged.value.first
        val to = _lastPositionsChanged.value.second
        fromId?.let {
            to?.let {
                profileManager.changeAlbumPosition(fromId, to).on(
                    success = {
                        _lastPositionsChanged.emit(null to null)
                    },
                    loading = {
                        _lastPositionsChanged.emit(null to null)
                    },
                    error = {
                        _lastPositionsChanged.emit(null to null)
                    }
                )
            }
        }

    }
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    suspend fun setSelectedImageId(imageId:String){
        _selectedImageId.emit(imageId)
    }
}
