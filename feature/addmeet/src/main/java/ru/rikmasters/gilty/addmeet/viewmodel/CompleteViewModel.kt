package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel

class CompleteViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    
    private val addMeet by lazy { meetManager.addMeetFlow.state(null) }
    
    private val _meet = MutableStateFlow<MeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _meet.emit(it?.map(getProfile()))
                _online.emit(it?.isOnline ?: false)
            }
        }
    }
    
    suspend fun addMeet() {
        val id = meetManager.addMeet(
            addMeet.value!!
        )
        _meet.emit(meet.value?.copy(id))
    }
    
    private suspend fun getProfile() = profileManager
        .getProfile(false).map()
}