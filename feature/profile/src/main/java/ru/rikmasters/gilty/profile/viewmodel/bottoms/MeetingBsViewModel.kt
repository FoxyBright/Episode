package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.auth.manager.ProfileManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation
import ru.rikmasters.gilty.shared.common.meetBS.MeetNavigation.*
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

class MeetingBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _screen = MutableStateFlow(MEET)
    val screen = _screen.asStateFlow()
    
    // todo добавить в пару адресата
    private val _stack = MutableStateFlow(emptyList<MeetNavigation>())
    val stack = _stack.asStateFlow()
    
    suspend fun navigate(screen: MeetNavigation, params: String = "") {
        _stack.emit(stack.value + screen)
        when(screen) {
            PARTICIPANTS -> _memberList.emit(meetManager.getMeetMembers(params))
            ORGANIZER -> {
                _profile.emit(profileManager.getUser(params))
                _observe.emit(profile.value.isWatching == true)
            }
            
            MEET -> drawMeet(params)
            COMPLAINTS -> {}
        }
        _screen.emit(screen)
    }
    
    suspend fun clearStack() {
        _stack.emit(emptyList())
    }
    
    suspend fun navigateBack() {
        if(stack.value.isNotEmpty()) {
            _stack.emit(stack.value - stack.value[stack.value.size - 1])
            _screen.emit(
                if(stack.value.isEmpty()) MEET
                else stack.value.last(),
            )
        }
    }
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _memberList = MutableStateFlow(listOf<MemberModel>())
    val memberList = _memberList.asStateFlow()
    
    private val _distance = MutableStateFlow("")
    val distance = _distance.asStateFlow()
    
    private suspend fun drawMeet(meetId: String) {
        _meet.emit(meetManager.getDetailedMeet(meetId))
        if(meet.value?.isOnline == true)
            _distance.emit(distanceCalculator(meet.value!!))
        _memberList.emit(meetManager.getMeetMembers(meetId))
    }
    
    suspend fun menuDismiss(state: Boolean) {
        _menu.emit(state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun respondForMeet(meetId: String) {
        makeToast("Вы откликнулись на meet")
    }
    
    suspend fun sharedMeet(meetId: String) {
        makeToast("Поделиться")
    }
    
    suspend fun meetPlaceClick(location: LocationModel?) {
        makeToast("API карт пока отсутствует")
    }
    
    suspend fun leaveMeet(meetId: String) {
        meetManager.leaveMeet(meetId)
    }
    
    suspend fun canceledMeet(meetId: String) {
        meetManager.cancelMeet(meetId)
    }
    
    private val _profile = MutableStateFlow(DemoProfileModel)
    val profile = _profile.asStateFlow()
    
    private val _observe = MutableStateFlow(false)
    val observe = _observe.asStateFlow()
    
    suspend fun observeUser(state: Boolean, userId: String) {
        if(state) profileManager.subscribeToUser(userId)
        else profileManager.unsubscribeFromUser(userId)
        _observe.emit(profileManager.getUser(userId).isWatching == true)
    }
    
    private val _userActualMeets = MutableStateFlow(listOf<MeetingModel>())
    val userActualMeets = _userActualMeets.asStateFlow()
    
    suspend fun getUserActualMeets(userId: String) {
        _userActualMeets.emit(
            meetManager.getUserActualMeets(userId)
        )
    }
}