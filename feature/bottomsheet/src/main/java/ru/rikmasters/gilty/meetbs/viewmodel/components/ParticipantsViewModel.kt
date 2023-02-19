package ru.rikmasters.gilty.meetbs.viewmodel.components

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType
import ru.rikmasters.gilty.meetbs.viewmodel.ObserveViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel

class ParticipantsViewModel(
    
    private val observeVm: ObserveViewModel = ObserveViewModel(),
): ViewModel() {
    
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
    
    suspend fun onBack(old: Pair<String, String>) {
        observeVm.setUser(old.first)
        observeVm.setMeeting(old.second)
    }
    
    suspend fun navigate(
        nav: NavHostController,
        address: ObserveType,
        link: String,
    ) {
        observeVm.navigate(nav, address, link)
    }
}