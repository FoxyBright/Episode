package ru.rikmasters.gilty.notifications.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.notifications.viewmodel.NotificationViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel

class RespondsBsViewModel(
    
    val notificationVM: NotificationViewModel = NotificationViewModel(),
): ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _responds =
        MutableStateFlow(emptyList<MeetWithRespondsModel>())
    val responds = _responds.asStateFlow()
    
    private val _groupsStates = MutableStateFlow(emptyList<Int>())
    val groupsStates = _groupsStates.asStateFlow()
    
    suspend fun getResponds() = singleLoading {
        _responds.emit(profileManager.getResponds(RECEIVED))
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
        getResponds()
    }
    
    suspend fun acceptRespond(respondId: String) = singleLoading {
        profileManager.acceptRespond(respondId)
        getResponds()
    }
}