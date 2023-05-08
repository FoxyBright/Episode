package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class UserBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _meetType = MutableStateFlow(GROUP)
    val meetType = _meetType.asStateFlow()
    
    private val _profile = MutableStateFlow(ProfileModel())
    val profile = _profile.asStateFlow()
    
    private val _observe = MutableStateFlow(false)
    val observe = _observe.asStateFlow()
    
    private val _menuState = MutableStateFlow(false)
    val menuState = _menuState.asStateFlow()
    
    private val _isMyProfile = MutableStateFlow(false)
    val isMyProfile = _isMyProfile.asStateFlow()
    
    private val _userActualMeets = MutableStateFlow(listOf<MeetingModel>())
    val userActualMeets = _userActualMeets.asStateFlow()
    
    private val _viewerState = MutableStateFlow(false)
    val viewerState = _viewerState.asStateFlow()
    
    private val _viewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages = _viewerImages.asStateFlow()
    
    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()
    
    suspend fun changePhotoViewState(state: Boolean) {
        _viewerState.emit(state)
    }
    
    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _viewerImages.emit(list)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
    
    suspend fun checkMyProfile(userId: String) {
        _isMyProfile.emit(
            profileManager
                .getProfile(false)
                .id == userId
        )
    }
    
    suspend fun menuDismiss(state: Boolean) {
        _menuState.emit(state)
    }
    
    suspend fun getUserActualMeets(userId: String) {
        try {
            _userActualMeets.emit(
                meetManager.getUserActualMeets(userId)
            )
        } catch(e: Exception) {
            _meetType.emit(ANONYMOUS)
        }
    }
    
    suspend fun setMeetType(
        meetId: String,
    ) = singleLoading {
        _meetType.emit(
            if(meetId != "null")
                meetManager.getDetailedMeet(
                    meetId
                )?.type ?: GROUP
            else GROUP
        )
    }
    
    suspend fun setUser(userId: String) = singleLoading {
        _profile.emit(profileManager.getUser(userId))
    }
    
    suspend fun observeUser(state: Boolean, userId: String) =
        singleLoading {
            if(state) profileManager.subscribeToUser(userId)
            else profileManager.unsubscribeFromUser(userId)
            _observe.emit(profileManager.getUser(userId).isWatching == true)
        }
    
    suspend fun observeUser(state: Boolean?) {
        _observe.emit(state ?: false)
    }
}