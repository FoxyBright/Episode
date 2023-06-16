package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.shared.common.compressor.compress
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.io.File

class HiddenViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val context = getKoin().get<Context>()
    
    private val _photoList = MutableStateFlow(emptyList<AvatarModel>())
    val photoList = _photoList.asStateFlow()
    
    private val hiddenList = MutableStateFlow(emptyList<AvatarModel>())
    
    private val _photosAmount = MutableStateFlow(0)
    val photosAmount = _photosAmount.asStateFlow()

    private val _viewerState = MutableStateFlow(false)
    val viewerState = _viewerState.asStateFlow()

    private val _isDragging = MutableStateFlow(false)
    val isDragging = _isDragging.asStateFlow()

    private val _viewerType = MutableStateFlow(PhotoViewType.PHOTO)
    val viewerType = _viewerType.asStateFlow()

    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()

    private val _lastPositionsChanged = MutableStateFlow(Pair<String?, Int?>(null, null))

    suspend fun getHidden() = singleLoading {
        regManager.getProfile().hidden?.let {
            val response = regManager
                .getHidden(it.albumId).on(
                    success = { list -> list },
                    loading = { emptyList<AvatarModel>() to 0 },
                    error = { emptyList<AvatarModel>() to 0 }
                )
            _photoList.emit(response.first)
            hiddenList.emit(response.first)
            _photosAmount.emit(response.second)
        }
    }
    
    @Suppress("unused")
    suspend fun selectImage(image: AvatarModel) {
        if(!photoList.value.contains(image))
            _photoList.emit(photoList.value + image)
    }
    
    suspend fun deleteImage(image: AvatarModel) = singleLoading {
        if(photoList.value.contains(image))
            _photoList.emit(photoList.value - image)
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
    suspend fun movePhoto(fromOr: ItemPosition, toOr: ItemPosition) {
        (fromOr.index - 1).let { from ->
            (toOr.index - 1).let { to ->
                if (_photoList.value.size > from && _photoList.value.size > to) {
                    val fromId = _photoList.value[from].id
                    val newList = _photoList.value.toMutableList().apply {
                        add(to, removeAt(from))
                    }
                    _photoList.emit(newList)

                    var newLastChanged = Pair<String?, Int?>(null, null)
                    newLastChanged = if(_lastPositionsChanged.value.first == null){
                        _lastPositionsChanged.emit(_lastPositionsChanged.value.copy(first = fromId))
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
    suspend fun onNext() = singleLoading {
        regManager.deleteHidden(hiddenList.value.map { it.thumbnail.url })
        regManager.addHidden(photoList.value.map {
            File(it.thumbnail.url).compress(context)
        }).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    suspend fun onIsDraggingChange(value:Boolean){
        _isDragging.emit(value)
    }
    suspend fun movePhotoRemote() {
        val fromId = _lastPositionsChanged.value.first
        val to = _lastPositionsChanged.value.second
        fromId?.let {
            to?.let {
                regManager.changeAlbumPosition(fromId, to).on(
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
}