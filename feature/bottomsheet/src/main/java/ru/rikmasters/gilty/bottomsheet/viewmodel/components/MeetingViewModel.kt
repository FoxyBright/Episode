package ru.rikmasters.gilty.bottomsheet.viewmodel.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.LocationModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class MeetingViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _memberList = MutableStateFlow(listOf<UserModel>())
    val memberList = _memberList.asStateFlow()
    
    private val _hidden = MutableStateFlow(false)
    val hidden = _hidden.asStateFlow()
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    private val _comment = MutableStateFlow("")
    val comment = _comment.asStateFlow()
    
    private val _distance = MutableStateFlow("")
    val distance = _distance.asStateFlow()
    
    suspend fun drawMeet(meetId: String) {
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
    
    suspend fun respondForMeet(meetId: String) {
        meetManager.respondOfMeet(
            meetId, comment.value.ifBlank { null }, hidden.value
        )
    }
    
    suspend fun meetPlaceClick(location: LocationModel?) {
        makeToast("API карт пока отсутствует $location")
    }
    
    suspend fun leaveMeet(meetId: String) {
        meetManager.leaveMeet(meetId)
    }
    
    suspend fun canceledMeet(meetId: String) {
        meetManager.cancelMeet(meetId)
    }
}