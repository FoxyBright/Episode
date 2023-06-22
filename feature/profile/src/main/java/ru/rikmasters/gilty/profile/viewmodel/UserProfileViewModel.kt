package ru.rikmasters.gilty.profile.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.profile.models.MeetingsType.ACTUAL
import ru.rikmasters.gilty.profile.models.MeetingsType.HISTORY
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW_INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel.DEFAULT
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class UserProfileViewModel: ViewModel(), PullToRefreshTrait {
    
    private val regManager by inject<RegistrationManager>()
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    private val chatManager by inject<ChatManager>()
    private val context = getKoin().get<Context>()
    
    private val _occupied =
        MutableStateFlow(false)
    val occupied =
        _occupied.asStateFlow()
    
    private val _photoAlertState =
        MutableStateFlow(false)
    val photoAlertState =
        _photoAlertState.asStateFlow()
    
    private val _menu =
        MutableStateFlow(false)
    val menu =
        _menu.asStateFlow()
    
    private val _historyState =
        MutableStateFlow(false)
    val historyState =
        _historyState.asStateFlow()
    
    private val _profile =
        MutableStateFlow<ProfileModel?>(null)
    val profile =
        _profile.asStateFlow()
    
    private val _description =
        MutableStateFlow<String?>(null)
    private val description =
        _description.asStateFlow()
    
    private val _photoViewState =
        MutableStateFlow(false)
    val photoViewState =
        _photoViewState.asStateFlow()
    
    private val _activeAlbumId =
        MutableStateFlow<Int?>(null)
    val activeAlbumId =
        _activeAlbumId.asStateFlow()
    
    private val _viewerImages =
        MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages =
        _viewerImages.asStateFlow()
    
    private val _viewerSelectImage =
        MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage =
        _viewerSelectImage.asStateFlow()
    
    private val refresh =
        MutableStateFlow(false)
    private val _username =
        MutableStateFlow("")
    val username =
        _username.asStateFlow()
    
    private val _alert =
        MutableStateFlow(false)
    val alert =
        _alert.asStateFlow()
    
    private val _lastRespond =
        MutableStateFlow(LastRespond())
    val lastRespond =
        _lastRespond.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val desc = description
        .debounce(250)
        .onEach { updateDescription() }
        .state(_description.value, Eagerly)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val meetsTest by lazy {
        refresh.flatMapLatest {
            profileManager.getUserMeets(ACTUAL)
        }.cachedIn(coroutineScope)
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val historyMeetsTest by lazy {
        refresh.flatMapLatest {
            profileManager.getUserMeets(HISTORY)
        }.cachedIn(coroutineScope)
    }
    
    private val _unreadMessages =
        MutableStateFlow(
            lazy {
                val count = context.getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).getInt("unread_messages", 0)
                if(count > 0) NEW_INACTIVE else INACTIVE
            }.value
        )
    val unreadMessages =
        _unreadMessages.asStateFlow()
    
    private val _unreadNotification =
        MutableStateFlow(
            lazy {
                val count = context.getSharedPreferences(
                    "sharedPref", MODE_PRIVATE
                ).getInt("unread_notification", 0)
                if(count > 0) NEW_INACTIVE else INACTIVE
            }.value
        )
    val unreadNotification =
        _unreadNotification.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val usernameDebounced = username
        .debounce(250)
        .onEach { name ->
            val occupied =
                if(name == profile.value?.username) false
                else regManager.isNameOccupied(name).on(
                    success = {
                        it
                    },
                    loading = { false },
                    error = { false }
                )
            _occupied.emit(occupied)
        }
        .state(_username.value, Eagerly)
    
    suspend fun changePhotoViewState(state: Boolean) {
        _photoViewState.emit(state)
    }
    
    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _viewerImages.emit(list)
    }
    
    suspend fun updateUserData() {
        _profile.emit(
            profileManager.getProfile(
                forceWeb = true
            )
        )
    }
    
    suspend fun changeActiveAlbumId(id: Int?) {
        _activeAlbumId.emit(id)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
    
    suspend fun updateUsername() {
        if(!occupied.value) regManager
            .userUpdateData(
                username = username.value,
                aboutMe = description.value
            )
            .on(
                success = {},
                loading = {},
                error = {}
            )
    }
    
    suspend fun changeUsername(name: String) {
        if(name.length <= 21) _username.emit(name)
    }
    
    suspend fun setUnreadMessages(hasUnread: Boolean) {
        _unreadMessages.emit(if(hasUnread) NEW_INACTIVE else INACTIVE)
    }
    
    suspend fun setUnreadNotifications(hasUnread: Boolean) {
        _unreadNotification.emit(
            if(hasUnread) NEW_INACTIVE
            else INACTIVE
        )
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
        if(text.length <= 120) {
            _description.emit(text)
            _profile.emit(
                profile.value?.copy(
                    aboutMe = description.value
                )
            )
        }
    }
    
    suspend fun updateDescription() {
        regManager.userUpdateData(
            username = username.value,
            aboutMe = description.value
        ).on(
            success = {},
            loading = {},
            error = {}
        )
    }
    
    suspend fun getUnread() = chatManager
        .updateUnread(context)
    
    override suspend fun forceRefresh() =
        singleLoading {
            setUserDate(true)
            refresh.value = !refresh.value
        }
    
    suspend fun setUserDate(
        forceWeb: Boolean,
    ) = singleLoading {
        val user = profileManager
            .getProfile(forceWeb)
        _username.emit(user.username ?: "")
        _description.emit(user.aboutMe ?: "")
        _lastRespond.emit(
            LastRespond(
                image = user.respondsImage?.url,
                isOnline = user.isOnline ?: false,
                group = user.group ?: DEFAULT,
                count = user.respondsCount ?: 0,
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
        _historyState.emit(!historyState.value)
    }
    
    suspend fun menuDispose(state: Boolean) {
        _menu.emit(state)
    }
}