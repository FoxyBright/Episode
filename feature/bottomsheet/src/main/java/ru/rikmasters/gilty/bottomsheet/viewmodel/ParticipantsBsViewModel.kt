package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class ParticipantsBsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _participants =
        MutableStateFlow(emptyList<UserModel>())
    val participants = _participants.asStateFlow()
    
    suspend fun getParticipants(meetId: String) = singleLoading {
        _participants.emit(meetManager.getMeetMembers(meetId, true))
    }
    
    suspend fun getMeet(meetId: String) = singleLoading {
        _meet.emit(meetManager.getDetailedMeet(meetId))
    }
}