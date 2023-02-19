package ru.rikmasters.gilty.meetbs.viewmodel.components

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetbs.presentation.ui.ObserveType
import ru.rikmasters.gilty.meetbs.viewmodel.ObserveViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.MeetType.GROUP
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class UserViewModel(
    
    private val observeVm: ObserveViewModel = ObserveViewModel(),
): ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _meetType = MutableStateFlow(GROUP)
    val meetType = _meetType.asStateFlow()
    
    private val _profile = MutableStateFlow(ProfileModel.empty)
    val profile = _profile.asStateFlow()
    
    private val _observe = MutableStateFlow(false)
    val observe = _observe.asStateFlow()
    
    private val _userActualMeets = MutableStateFlow(listOf<MeetingModel>())
    val userActualMeets = _userActualMeets.asStateFlow()
    
    suspend fun getUserActualMeets(userId: String) {
        _userActualMeets.emit(
            meetManager.getUserActualMeets(userId)
        )
    }
    
    suspend fun setMeetType(meetId: String) = singleLoading {
        meetManager.getDetailedMeet(meetId).type
    }
    
    suspend fun setUser(userId: String) = singleLoading {
        _profile.emit(profileManager.getUser(userId))
    }
    
    suspend fun observeUser(state: Boolean, userId: String) = singleLoading {
        if(state) profileManager.subscribeToUser(userId)
        else profileManager.unsubscribeFromUser(userId)
        _observe.emit(profileManager.getUser(userId).isWatching == true)
    }
    
    suspend fun observeUser(state: Boolean?) {
        _observe.emit(state ?: false)
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