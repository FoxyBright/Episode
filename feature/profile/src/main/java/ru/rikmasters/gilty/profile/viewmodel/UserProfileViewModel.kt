package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel.ImageType.AVATAR
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel.ImageType.HIDDEN
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.enumeration.ProfileType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

class UserProfileViewModel: ViewModel() {
    
    //FIXME тут данные /////////////////////////////////////
    
    private val profileManager by inject<ProfileManager>()
    
    private val profileModel = DemoProfileModel
    private val meetingList = DemoMeetingList
    private val respond = DemoMeetingModel
    private val navBarStateList = listOf(
        INACTIVE, NEW, INACTIVE, NEW, ACTIVE
    )
    
    //TODO/////////////////////////////////////////////////////////
    
    private val _avatar = MutableStateFlow(profileModel.avatar.id)
    val avatar = _avatar.asStateFlow()
    
    private val _hidden = MutableStateFlow(profileModel.avatar.id)
    val hidden = _hidden.asStateFlow()
    
    private val _emoji = MutableStateFlow(profileModel.emoji)
    val emoji = _emoji.asStateFlow()
    
    private val _age = MutableStateFlow(0)
    val age = _age.asStateFlow()
    
    private val _rating = MutableStateFlow("0.0")
    val rating = _rating.asStateFlow()
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _meets = MutableStateFlow(meetingList)
    val meets = _meets.asStateFlow()
    
    private val _history = MutableStateFlow(false)
    val history = _history.asStateFlow()
    
    private val _profile = MutableStateFlow(profileModel)
    val profile = _profile.asStateFlow()
    
    private val _description = MutableStateFlow(profileModel.aboutMe)
    val description = _description.asStateFlow()
    
    private val _username = MutableStateFlow(profileModel.username)
    val username = _username.asStateFlow()
    
    private val _complaintsAlert = MutableStateFlow(false)
    val complaintsAlert = _complaintsAlert.asStateFlow()
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private val _lastRespond = MutableStateFlow(respond)
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _observers = MutableStateFlow(0)
    val observers = _observers.asStateFlow()
    
    private val _observed = MutableStateFlow(0)
    val observed = _observed.asStateFlow()
    
    // TODO ИЗМЕНИТЬ /////////////////////////////////////////
    
    private val _observersSelectTab = MutableStateFlow(listOf(true, false))
    val observersSelectTab = _observersSelectTab.asStateFlow()
    
    private val _respondsSelectTab = MutableStateFlow(listOf(true, false))
    val respondsSelectTab = _respondsSelectTab.asStateFlow()
    
    private val _observeGroupStates = MutableStateFlow(listOf(true, false))
    val observeGroupStates = _observeGroupStates.asStateFlow()
    
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()
    
    suspend fun changeObserversTab(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(observersSelectTab.value.size) { list.add(it == tab) }
        _observersSelectTab.emit(list)
    }
    
    suspend fun changeRespondsTab(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(respondsSelectTab.value.size) { list.add(it == tab) }
        _respondsSelectTab.emit(list)
    }
    
    suspend fun changeObserveGroupStates(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(observeGroupStates.value.size) { list.add(it == tab) }
        _observeGroupStates.emit(list)
    }
    
    // TODO /////////////////////////////////////////////////
    
    suspend fun drawProfile (){
        val profile = profileManager.getProfile()
        logD("profile WEB ----->>> $profile")
        val userProfile = profile.map()
        logD("profile THIS ----->>> $userProfile")
    
        _profileState.emit(ProfileState(
            name = "${userProfile.username}, ${userProfile.age}",
            profilePhoto = userProfile.avatar.id,
            hiddenPhoto = profile.album_private?.preview?.thumbnail?.url.toString(),
            description = userProfile.aboutMe,
            rating = userProfile.rating.average,
            observers = profile.count_watchers?: 0,
            observed = profile.count_watching?: 0,
            emoji = userProfile.rating.frequent,
            profileType = ProfileType.USERPROFILE,
            enabled = true,
        ))
    }
    
    
    private suspend fun navBarSetStates(
        states: List<NavIconState>
    ) {
        _navBar.emit(states)
    }
    
    suspend fun navBarNavigate(point: Int): String {
        val list = arrayListOf<NavIconState>()
        repeat(navBar.value.size) {
            list.add(
                when {
                    navBar.value[it] == NEW -> NEW
                    it == point -> ACTIVE
                    else -> INACTIVE
                }
            )
        }
        navBarSetStates(list)
        return when(point) {
            0 -> "main/meetings"
            1 -> "notification/list"
            2 -> "addmeet/category"
            3 -> "chats/main"
            else -> "profile/main"
        }
    }
    
    enum class ImageType { HIDDEN, AVATAR }
    
    suspend fun changeImage(
        image: String,
        type: ImageType
    ) {
        when(type) {
            HIDDEN -> _hidden.emit(image)
            AVATAR -> _avatar.emit(image)
        }
    }
    
    suspend fun setComplaintAlertState(state: Boolean) {
        _complaintsAlert.emit(state)
    }
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
    }
    
    suspend fun changeUsername(text: String) {
        _username.emit(text)
        drawProfile()
    }
    
    suspend fun showHistory() {
        _history.emit(!history.value)
    }
    
    suspend fun menuDispose(state: Boolean) {
        _menu.emit(state)
    }
}