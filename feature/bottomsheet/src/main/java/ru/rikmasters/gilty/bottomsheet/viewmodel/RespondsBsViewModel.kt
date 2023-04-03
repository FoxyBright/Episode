package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.SENT
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel

class RespondsBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _responds =
        MutableStateFlow(emptyList<MeetWithRespondsModel>())
    val responds = _responds.asStateFlow()
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _groupsStates = MutableStateFlow(emptyList<Int>())
    val groupsStates = _groupsStates.asStateFlow()
    
    suspend fun selectTab(tab: Int) {
        _tabs.emit(tab)
    }
    
    suspend fun getResponds(meetId: String?) = singleLoading {
        meetId?.let {
            val resList = profileManager.getMeetResponds(it)
            _responds.emit(listOf(MeetWithRespondsModel(resList)))
        } ?: run {
            _responds.emit(
                profileManager.getResponds(
                    if(tabs.value == 0) SENT
                    else RECEIVED
                )
            )
        }
    }
    
    suspend fun selectRespondsGroup(index: Int) {
        val list = groupsStates.value
        _groupsStates.emit(
            if(list.contains(index))
                list - index
            else list + index
        )
    }
    
    suspend fun cancelRespond(respondId: String) = singleLoading {
        profileManager.cancelRespond(respondId)
    }
    
    @Suppress("unused")
    suspend fun deleteRespond(respondId: String) = singleLoading {
        profileManager.deleteRespond(respondId)
    }
    
    suspend fun acceptRespond(respondId: String) = singleLoading {
        profileManager.acceptRespond(respondId)
    }
}