package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.MeetingsType.ACTUAL
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.MeetingsType.HISTORY
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.INACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.NavIconState.NEW
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class UserProfileViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val regManager by inject<RegistrationManager>()
    
    private suspend fun getUserProfile() = profileManager.getProfile()
    
    private val profileModel = ProfileModel.empty
    
    private val navBarStateList = listOf(
        INACTIVE, NEW, INACTIVE, NEW, ACTIVE
    )
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _errorConnection = MutableStateFlow(false)
    val errorConnection = _errorConnection.asStateFlow()
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _age = MutableStateFlow(0)
    private val age = _age.asStateFlow()
    
    private val _meets = MutableStateFlow(listOf<MeetingModel>())
    val meets = _meets.asStateFlow()
    
    private val _meetsHistory = MutableStateFlow(listOf<MeetingModel>())
    val meetsHistory = _meetsHistory.asStateFlow()
    
    private val _history = MutableStateFlow(false)
    val history = _history.asStateFlow()
    
    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()
    
    private val _description = MutableStateFlow(profileModel.aboutMe)
    private val description = _description.asStateFlow()
    
    private val _username = MutableStateFlow(profileModel.username)
    private val username = _username.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _lastRespond = MutableStateFlow(Pair(0, ""))
    val lastRespond = _lastRespond.asStateFlow()
    
    private val _navBar = MutableStateFlow(navBarStateList)
    val navBar = _navBar.asStateFlow()
    
    private suspend fun navBarSetStates(
        states: List<NavIconState>,
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
    
    suspend fun errorConnection(state: Boolean) {
        _errorConnection.emit(state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
        updateProfile()
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
    
    private suspend fun getUserMeets() {
        _meets.emit(profileManager.getUserMeets(ACTUAL))
        _meetsHistory.emit(profileManager.getUserMeets(HISTORY))
    }
    
    suspend fun setUserDate() {
        val user = getUserProfile()
        _age.emit(user.age)
        _username.emit("${user.username}, ${user.age}")
        _description.emit(user.aboutMe ?: "")
        _lastRespond.emit(Pair(user.respondsCount ?: 0, user.respondsImage?.id ?: ""))
        _profile.emit(
            user.copy(
                username = username.value,
                aboutMe = description.value
            )
        )
        getUserMeets()
    }
    
    suspend fun changeUsername(name: String) {
        if(!name.contains(',')) return
        val text = name.substringBefore(',')
        _username.emit("$text, ${age.value}")
        updateProfile()
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