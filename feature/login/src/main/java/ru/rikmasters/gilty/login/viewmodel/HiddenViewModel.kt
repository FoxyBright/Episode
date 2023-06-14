package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType
import ru.rikmasters.gilty.shared.common.dragGrid.ItemPosition
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.shared.compress
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

    private val _viewerType = MutableStateFlow(PhotoViewType.PHOTO)
    val viewerType = _viewerType.asStateFlow()

    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()
    
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
                    regManager.changeAlbumPosition(fromId, to + 1).on(
                        success = {},
                        loading = {},
                        error = {}
                    )
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
}