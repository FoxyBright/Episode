package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class HiddenBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _photoList = MutableStateFlow(listOf<AvatarModel>())
    val photoList = _photoList.asStateFlow()
    
    suspend fun uploadPhotoList() {
        _photoList.emit(profileManager.getProfileHiddens())
    }
    
    suspend fun deleteImage(image: AvatarModel) {
        profileManager.deleteHidden(image)
        uploadPhotoList()
    }
}