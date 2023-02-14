package ru.rikmasters.gilty.notifications.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.*
import ru.rikmasters.gilty.shared.common.meetBS.Navigator
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

class ObserveBsViewModel(
    
    private val notificationVm: NotificationViewModel = NotificationViewModel(),
): ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _userActualMeets = MutableStateFlow(listOf<MeetingModel>())
    val userActualMeets = _userActualMeets.asStateFlow()
    
    private val _memberList = MutableStateFlow(listOf<UserModel>())
    val memberList = _memberList.asStateFlow()
    
    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _navigator = MutableStateFlow<Navigator?>(null)
    val navigator = _navigator.asStateFlow()
    
    private val _stack = MutableStateFlow(emptyList<Navigator>())
    val stack = _stack.asStateFlow()
    
    private val _profile = MutableStateFlow(DemoProfileModel)
    val profile = _profile.asStateFlow()
    
    private val _observe = MutableStateFlow(false)
    val observe = _observe.asStateFlow()
    
    private val _hidden = MutableStateFlow(false)
    val hidden = _hidden.asStateFlow()
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _comment = MutableStateFlow("")
    val comment = _comment.asStateFlow()
    
    private val _distance = MutableStateFlow("")
    val distance = _distance.asStateFlow()
    
    suspend fun navigate(
        screen: MeetNavigation,
        params: String = "",
    ) {
        val nav = Navigator(screen, params)
        _stack.emit(stack.value + nav)
        when(screen) {
            PARTICIPANTS -> _memberList.emit(meetManager.getMeetMembers(params))
            MEET -> drawMeet(params)
            COMPLAINTS -> {}
            ORGANIZER -> {
                _profile.emit(profileManager.getUser(params))
                _observe.emit(profile.value.isWatching == true)
            }
        }
        _navigator.emit(nav)
    }
    
    suspend fun clearStack() {
        _stack.emit(emptyList())
    }
    
    suspend fun navigateBack() {
        if(stack.value.isNotEmpty()) {
            _stack.emit(stack.value - stack.value.last())
            _navigator.emit(stack.value.last())
        }
    }
    
    private suspend fun drawMeet(meetId: String) {
        _meet.emit(meetManager.getDetailedMeet(meetId))
        if(meet.value?.isOnline == true)
            _distance.emit(distanceCalculator(meet.value!!))
        _memberList.emit(meetManager.getMeetMembers(meetId))
    }
    
    suspend fun changeHidden(state: Boolean) {
        _hidden.emit(state)
    }
    
    suspend fun changeComment(text: String) {
        _comment.emit(text)
    }
    
    suspend fun clearComment() {
        _comment.emit("")
    }
    
    suspend fun menuDismiss(state: Boolean) {
        _menu.emit(state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
//        notificationVm.alertDismiss(state)
    }
    
    suspend fun respondForMeet(meetId: String) {
        meetManager.respondOfMeet(
            meetId, comment.value.ifBlank { null },
            hidden.value
        )
    }
    
    suspend fun meetPlaceClick(location: LocationModel?) {
        makeToast("API карт пока отсутствует $location")
    }
    
    suspend fun observeUser(state: Boolean, userId: String) {
        if(state) profileManager.subscribeToUser(userId)
        else profileManager.unsubscribeFromUser(userId)
        _observe.emit(profileManager.getUser(userId).isWatching == true)
    }
    
    suspend fun getUserActualMeets(userId: String) {
        _userActualMeets.emit(
            meetManager.getUserActualMeets(userId)
        )
    }
}