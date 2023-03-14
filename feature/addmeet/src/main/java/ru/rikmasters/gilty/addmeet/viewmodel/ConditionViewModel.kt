package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType

var Price: String = ""
var Online: Boolean = false
var Hidden: Boolean = false
var RestrictChat: Boolean = false
var MeetingType: MeetType? = null
var Condition: ConditionType? = null

class ConditionViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    val addMeet by lazy { manager.addMeetFlow.state(null) }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    suspend fun changeOnline(state: Boolean) {
        manager.update(isOnline = state)
    }
    
    suspend fun changeHidden(state: Boolean) {
        manager.update(photoAccess = state)
    }
    
    suspend fun changeMeetType(type: Int) {
        manager.update(type = MeetType.get(type))
    }
    
    suspend fun changeCondition(condition: Int) {
        manager.update(condition = ConditionType.get(condition))
    }
    
    suspend fun changeForbiddenChat(state: Boolean) {
        manager.update(chatForbidden = state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changePrice(text: String) {
        manager.update(price = text)
    }
    
    suspend fun clearPrice() {
        manager.update(price = "")
    }
    
    suspend fun clearBase() {
        manager.clearBase()
    }
}