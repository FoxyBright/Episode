package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel.ImageType.AVATAR
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel.ImageType.HIDDEN
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.profile.DemoEmptyProfileModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.model.profile.getEmoji

class UserProfileViewModel: ViewModel() {

    private val profileManager by inject<ProfileManager>()
    private val regManager by inject<RegistrationManager>()
    private suspend fun getUserProfile() = profileManager.getProfile()
    
    private val profileModel = DemoEmptyProfileModel
    private val meetingList = DemoMeetingList
    
    private val navBarStateList = listOf(
        INACTIVE, NEW, INACTIVE, NEW, ACTIVE
    )
    
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
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _meets = MutableStateFlow(meetingList)
    val meets = _meets.asStateFlow()
    
    private val _history = MutableStateFlow(false)
    val history = _history.asStateFlow()
    
    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()
    
    private val _description = MutableStateFlow(profileModel.aboutMe)
    val description = _description.asStateFlow()
    
    private val _username = MutableStateFlow(profileModel.username)
    val username = _username.asStateFlow()
    
    private val _complaintsAlert = MutableStateFlow(false)
    val complaintsAlert = _complaintsAlert.asStateFlow()
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private val _lastRespond = MutableStateFlow(Pair(0, ""))
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _observers = MutableStateFlow(0)
    val observers = _observers.asStateFlow()
    
    private val _observed = MutableStateFlow(0)
    val observed = _observed.asStateFlow()
    
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
    
    private suspend fun changeImage(
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
        regManager.userUpdateData(
            username.value.substringBefore(','),
            description.value,
            age.value
        )
    }
    
    suspend fun setUserDate() {
        val user = getUserProfile()
        _username.emit("${user.username}, ${user.age}")
        _description.emit(user.about_me ?: "")
        _rating.emit(user.average.toString())
        _age.emit(user.age ?: 0)
        _emoji.emit(getEmoji(user.emoji_type.toString()))
        _observed.emit(user.count_watching ?: 0)
        _observers.emit(user.count_watchers ?: 0)
        _lastRespond.emit(Pair(user.responds?.count ?: 0, user.responds?.thumbnail?.url ?: ""))
        user.avatar?.url?.let { changeImage(it, AVATAR) }
        user.album_private?.preview?.url?.let { changeImage(it, HIDDEN) }
    }
    
    suspend fun changeUsername(name: String) {
        if(!name.contains(',')) return
        val text = name.substringBefore(',')
        _username.emit("$text, ${age.value}")
        _occupied.emit(regManager.isNameOccupied(text))
        if(!occupied.value) regManager.userUpdateData(
            text, description.value, age.value
        )
    }
    
    suspend fun showHistory() {
        _history.emit(!history.value)
    }
    
    suspend fun menuDispose(state: Boolean) {
        _menu.emit(state)
    }
}