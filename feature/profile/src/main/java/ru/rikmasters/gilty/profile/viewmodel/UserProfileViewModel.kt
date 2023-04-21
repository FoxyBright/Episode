package ru.rikmasters.gilty.profile.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.profile.models.MeetingsType.ACTUAL
import ru.rikmasters.gilty.profile.models.MeetingsType.HISTORY
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW_INACTIVE
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class UserProfileViewModel: ViewModel(), PullToRefreshTrait {
    
    private val regManager by inject<RegistrationManager>()
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _photoAlertState = MutableStateFlow(false)
    val photoAlertState = _photoAlertState.asStateFlow()
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _age = MutableStateFlow(0)
    private val age = _age.asStateFlow()
    
    private val _history = MutableStateFlow(false)
    val history = _history.asStateFlow()
    
    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()
    
    private val _description = MutableStateFlow<String?>(null)
    private val description = _description.asStateFlow()
    
    private val _username = MutableStateFlow<String?>(null)
    private val username = _username.asStateFlow()
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val meetsTest by lazy {
        refresh.flatMapLatest {
            profileManager.getUserMeets(ACTUAL).cachedIn(coroutineScope)
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val historyMeetsTest by lazy {
        refresh.flatMapLatest {
            profileManager.getUserMeets(HISTORY).cachedIn(coroutineScope)
        }
    }
    
    private val refresh = MutableStateFlow(false)
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val usernameDebounced = username
        .debounce(250)
        .onEach {
            it?.substringBefore(',')?.let { name ->
                _occupied.emit(
                    regManager.isNameOccupied(name)
                            && profileManager
                        .getProfile(false)
                        .username != name
                )
            }
        }
        .state(_username.value, SharingStarted.Eagerly)
    
    private val _photoViewState = MutableStateFlow(false)
    val photoViewState = _photoViewState.asStateFlow()
    
    private val _viewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages = _viewerImages.asStateFlow()
    
    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()
    
    suspend fun changePhotoViewState(state: Boolean) {
        _photoViewState.emit(state)
    }
    
    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _viewerImages.emit(list)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
    
    suspend fun updateUsername() {
        if(!occupied.value) regManager.userUpdateData(
            username.value?.substringBefore(','),
            description.value, age.value
        )
    }
    
    suspend fun changeUsername(name: String) {
        if(!name.contains(',')) return
        val text = name.substringBefore(',')
        _username.emit(
            "$text, ${
                if(age.value in 18..99) {
                    ", ${age.value}"
                } else ""
            }"
        )
        updateProfile()
    }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _lastRespond =
        MutableStateFlow<Pair<Int, String?>>(Pair(0, null))
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _unreadMessages = MutableStateFlow(
        lazy {
            val count = getKoin().get<Context>().getSharedPreferences(
                "sharedPref", ComponentActivity.MODE_PRIVATE
            ).getInt("unread_messages", 0)
            if(count > 0) NEW_INACTIVE else INACTIVE
        }.value
    )
    val unreadMessages = _unreadMessages.asStateFlow()
    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(if(hasUnread) NEW_INACTIVE else INACTIVE)
    }
    
    suspend fun photoAlertDismiss(state: Boolean) {
        _photoAlertState.emit(state)
    }
    
    suspend fun navBarNavigate(point: Int) = when(point) {
        0 -> "main/meetings"
        1 -> "notification/list"
        2 -> {
            meetManager.clearAddMeet()
            "addmeet/category"
        }
        3 -> "chats/main"
        else -> "profile/main"
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
        updateProfile()
    }
    
    suspend fun updateDescription() {
        regManager.userUpdateData(
            username.value?.substringBefore(','),
            description.value
        )
    }
    
    private suspend fun updateProfile() {
        _profile.emit(
            profile.value?.copy(
                username = username.value,
                aboutMe = description.value
            )
        )
    }
    
    override suspend fun forceRefresh() {
        setUserDate(true)
        refresh.value = !refresh.value
    }
    
    suspend fun setUserDate(forceWeb: Boolean = true) = singleLoading {
        val user = profileManager.getProfile(forceWeb)
        _age.emit(user.age)
        _username.emit(
            "${user.username}${
                if(user.age in 18..99) {
                    ", ${user.age}"
                } else ""
            }"
        )
        _description.emit(user.aboutMe ?: "")
        _lastRespond.emit(
            Pair(
                user.respondsCount ?: 0,
                user.respondsImage?.url
            )
        )
        _profile.emit(
            user.copy(
                username = username.value,
                aboutMe = description.value
            )
        )
    }
    
    suspend fun showHistory() {
        _history.emit(!history.value)
    }
    
    suspend fun menuDispose(state: Boolean) {
        _menu.emit(state)
    }
}