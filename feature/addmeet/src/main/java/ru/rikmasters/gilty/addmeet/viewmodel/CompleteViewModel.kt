package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.meeting.AddMeetModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel

class CompleteViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    
    val addMeet by lazy { meetManager.addMeetFlow }
    
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
    
    suspend fun updateUserData() {
        val meet = meet.value?.copy(
            organizer = getProfile(true)
        )
        _meet.emit(meet)
    }
    
    suspend fun addMeet(addMeet: AddMeetModel): Boolean {
        val id = meetManager.addMeet(
            addMeet
        )
        
        if(id.contains("error")) {
            makeToast(id.substringAfter("error"))
            return false
        }
        
        val meet = meet.value?.copy(id)
        _meet.emit(meet)
        return true
    }
    
    suspend fun clearAddMeet() {
        AgeFrom = ""
        AgeTo = ""
        Gender = null
        Orientation = null
        Requirements = arrayListOf(
            RequirementModel(
                gender = null,
                ageMin = 0,
                ageMax = 0,
                orientation = null
            )
        )
        RequirementsType = 0
        meetManager.clearAddMeet()
    }
    
    private suspend fun getProfile(forceWeb: Boolean = false) =
        profileManager.getProfile(forceWeb).map()
}