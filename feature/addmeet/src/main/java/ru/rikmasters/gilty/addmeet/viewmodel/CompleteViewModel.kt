package ru.rikmasters.gilty.addmeet.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.AddMeetModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel

class CompleteViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    
    private val context = getKoin().get<Context>()
    
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
        meetManager.addMeet(addMeet).on(
            success = {
                _meet.emit(meet.value?.copy(it))
                return true
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
                return false
            }
        )
        return false
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