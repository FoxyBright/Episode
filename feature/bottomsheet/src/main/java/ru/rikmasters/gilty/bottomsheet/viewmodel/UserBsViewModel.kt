package ru.rikmasters.gilty.bottomsheet.viewmodel

import android.content.Context
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.ANONYMOUS
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class UserBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val context = getKoin().get<Context>()
    
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
    
    private val _avatarViewerState = MutableStateFlow(false)
    val avatarViewerState = _avatarViewerState.asStateFlow()

    private val _hiddenViewerState = MutableStateFlow(false)
    val hiddenViewerState = _hiddenViewerState.asStateFlow()
    
    private val _avatarViewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val avatarViewerImages = _avatarViewerImages.asStateFlow()

    private val _hiddenViewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val hiddenViewerImages = _hiddenViewerImages.asStateFlow()
    
    private val _avatarViewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val avatarViewerSelectImage = _avatarViewerSelectImage.asStateFlow()

    private val _hiddenViewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val hiddenViewerSelectImage = _hiddenViewerSelectImage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val organizerHiddenImages by lazy {
        combine(
            _profile
        ) { it }.flatMapLatest {
            //if(hiddenViewerState.value)
                profileManager.getHiddenPhotos(_profile.value.avatar?.albumId)
            //else
        }.cachedIn(coroutineScope)
    }

    suspend fun setPhotoViewState(state: Boolean) {
        _avatarViewerState.emit(state)
    }
    
    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _avatarViewerImages.emit(list)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _avatarViewerSelectImage.emit(photo)
    }

    suspend fun setHiddenPhotoViewState(state: Boolean) {
        _hiddenViewerState.emit(state)
    }

    suspend fun setHiddenPhotoViewImages(list: List<AvatarModel?>) {
        _hiddenViewerImages.emit(list)
    }

    suspend fun setHiddenPhotoViewSelected(photo: AvatarModel?) {
        _hiddenViewerSelectImage.emit(photo)
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
        meetManager.getUserActualMeets(userId).on(
            success = {
                try {
                    _userActualMeets.emit(it)
                } catch(e: Exception) {
                    _meetType.emit(ANONYMOUS)
                }
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun setMeetType(
        meetId: String,
    ) = singleLoading {
        if(meetId == "null" || meetId.isBlank()) {
            _meetType.emit(GROUP)
            return@singleLoading
        }
        meetManager.getDetailedMeet(meetId).on(
            success = { _meetType.emit(it.type) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun setUser(userId: String) = singleLoading {
        profileManager.getUser(userId).on(
            success = { _profile.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun observeUser(state: Boolean, userId: String) =
        singleLoading {
            if(state) profileManager
                .subscribeToUser(userId).on(
                    success = {},
                    loading = {},
                    error = {
                        context.errorToast(
                            it.serverMessage
                        )
                    }
                )
            else profileManager
                .unsubscribeFromUser(userId).on(
                    success = {},
                    loading = {},
                    error = {
                        context.errorToast(
                            it.serverMessage
                        )
                    }
                )
            profileManager.getUser(userId).on(
                success = {
                    _observe.emit(
                        it.isWatching == true
                    )
                },
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
        }
    
    suspend fun observeUser(state: Boolean?) {
        _observe.emit(state ?: false)
    }
}