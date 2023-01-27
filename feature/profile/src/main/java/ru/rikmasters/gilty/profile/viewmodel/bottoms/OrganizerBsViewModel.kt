package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingList
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.DemoProfileModel

class OrganizerBsViewModel: ViewModel() {
    
    private val _profile = MutableStateFlow(DemoProfileModel)
    val profile = _profile.asStateFlow()
    
    private val _observe = MutableStateFlow(false)
    val observe = _observe.asStateFlow()
    
    private val _meets = MutableStateFlow(listOf<MeetingModel>())
    val meets = _meets.asStateFlow()
    
    suspend fun drawOrganizer() {
        _observe.emit(false)
        _meets.emit(DemoMeetingList)
    }
    
    suspend fun observeUser(state: Boolean) {
        _observe.emit(state)
    }
}