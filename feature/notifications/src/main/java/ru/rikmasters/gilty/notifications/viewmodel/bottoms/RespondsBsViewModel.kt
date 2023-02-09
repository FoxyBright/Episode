package ru.rikmasters.gilty.notifications.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedShortRespondModelWithoutPhoto
import ru.rikmasters.gilty.shared.model.notification.ShortRespondModel

class RespondsBsViewModel: ViewModel() {
    
    private val _responds = MutableStateFlow(emptyList<ShortRespondModel>())
    val responds = _responds.asStateFlow()
    
    private val _groupsStates = MutableStateFlow(emptyList<Int>())
    val groupsStates = _groupsStates.asStateFlow()
    
    suspend fun getResponds() = singleLoading {
        val list = listOf(
            DemoReceivedShortRespondModelWithoutPhoto,
            DemoReceivedRespondsModel
        )
        _responds.emit(list)
    }
    
    suspend fun selectRespondsGroup(index: Int) {
        val list = groupsStates.value
        _groupsStates.emit(
            if(list.contains(index))
                list - index
            else list + index
        )
    }
    
    suspend fun cancelRespond(respond: ShortRespondModel) {
        makeToast("Втреча отклонена")
        val list = responds.value
        _responds.emit(
            if(list.contains(respond))
                list - respond
            else list + respond
        )
    }
    
    suspend fun acceptRespond(respond: ShortRespondModel) {
        makeToast("Втреча принята")
        val list = responds.value
        _responds.emit(
            if(list.contains(respond))
                list - respond
            else list + respond
        )
    }
}