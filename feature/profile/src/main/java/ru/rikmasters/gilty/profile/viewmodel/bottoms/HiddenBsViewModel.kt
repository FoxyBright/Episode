package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.auth.profile.Image
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel

class HiddenBsViewModel(
    
    private val profileVm: UserProfileViewModel

): ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _photoList = MutableStateFlow(listOf<Image>())
    val photoList = _photoList.asStateFlow()
    
    suspend fun uploadPhotoList() {
        _photoList.emit(profileManager.getProfileHiddens())
    }
    
    suspend fun deleteImage(image: Image) {
        profileManager.deleteHidden(image)
        uploadPhotoList()
    }
}