package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

class RespondsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _meeting = MutableStateFlow<FullMeetingModel?>(null)
    val meeting = _meeting.asStateFlow()
    
    suspend fun getMeet(meetId: String) {
        _meeting.emit(meetManager.getDetailedMeet(meetId))
    }
}