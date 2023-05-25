package ru.rikmasters.gilty.profile.viewmodel.bottoms

import android.content.Context
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast

class HiddenViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val context = getKoin().get<Context>()
    
    val images by lazy {
        profileManager.getHiddenPhotos().cachedIn(coroutineScope)
    }
    
    private val _photosAmount = MutableStateFlow(0)
    val photosAmount = _photosAmount.asStateFlow()
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
}
