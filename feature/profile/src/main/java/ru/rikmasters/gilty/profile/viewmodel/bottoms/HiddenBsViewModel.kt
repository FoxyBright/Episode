package ru.rikmasters.gilty.profile.viewmodel.bottoms

import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager

class HiddenBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    val images by lazy {
        profileManager.hiddenFlow.state(emptyList())
    }
    
    suspend fun uploadPhotoList(forceWeb: Boolean) {
        profileManager.getProfileHiddens(forceWeb)
    }
    
    suspend fun deleteImage(imageId: String) {
        profileManager.deleteHidden(imageId)
    }
}