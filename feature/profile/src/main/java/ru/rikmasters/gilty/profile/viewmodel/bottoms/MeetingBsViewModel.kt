package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel.MeetNavigation.MEET
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

class MeetingBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    enum class MeetNavigation { MEET, PROFILE, PARTICIPANTS, COMPLAINTS }
    
    private val _screen = MutableStateFlow(MEET)
    val screen = _screen.asStateFlow()
    
    private val _navigationStack = MutableStateFlow(emptyList<MeetNavigation>())
    private val navigationStack = _navigationStack.asStateFlow()
    suspend fun navigate(screen: MeetNavigation) {
        _navigationStack.emit(navigationStack.value + screen)
        _screen.emit(screen)
    }
    
    suspend fun navigateBack() {
        if(navigationStack.value.isNotEmpty()) {
            _navigationStack.emit(
                navigationStack.value - navigationStack.value[navigationStack.value.size - 1]
            )
            _screen.emit(
                if(navigationStack.value.isEmpty()) MEET
                else navigationStack.value.last()
            )
        }
    }
    
    
    // NAVIGATION
    
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _memberList = MutableStateFlow(listOf<MemberModel>())
    val memberList = _memberList.asStateFlow()
    
    private val _distance = MutableStateFlow("")
    val distance = _distance.asStateFlow()
    
    suspend fun drawMeet(meet: MeetingModel) {
        _memberList.emit(meetManager.getMeetMembers(meet.id))
        _distance.emit(distanceCalculator(meet))
    }
    
    suspend fun menuDismiss(state: Boolean) {
        _menu.emit(state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun outOfMeet() {
        makeToast("Вы покинули meet")
    }
    
    suspend fun cancelOfMeet() {
        makeToast("Вы отменили встречу")
    }
    
    suspend fun sharedMeet() {
        makeToast("Поделиться")
    }
    
    // ORGANIZER
    private val _profile = MutableStateFlow(DemoProfileModel)
    val profile = _profile.asStateFlow()
    
    private val _observe = MutableStateFlow(false)
    val observe = _observe.asStateFlow()
    
    private val _meets = MutableStateFlow(listOf<MeetingModel>())
    val meets = _meets.asStateFlow()
    
    suspend fun drawProfile() {
        _observe.emit(false)
        _meets.emit(DemoMeetingList)
    }
    
    suspend fun observeUser(state: Boolean) {
        _observe.emit(state)
    }
}