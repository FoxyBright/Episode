package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    
    val addMeet by lazy { manager.addMeetFlow }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _price.emit(it?.price ?: "")
            }
        }
    }
    
    suspend fun changePrice(text: String) {
        _price.emit(text)
        manager.update(price = text)
    }
    
    suspend fun clearPrice() {
        _price.emit("")
        manager.update(price = "")
    }
    
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
    
    suspend fun clearBase() {
        manager.clearBase()
    }
}