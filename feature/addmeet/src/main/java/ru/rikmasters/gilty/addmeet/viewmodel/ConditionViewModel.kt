package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType

var Price: String = ""
var Online: Boolean = false
var Hidden: Boolean = false
var RestrictChat: Boolean = false
var MeetingType: MeetType? = null
var Condition: ConditionType? = null

class ConditionViewModel: ViewModel() {
    
    private val _price = MutableStateFlow(Price)
    val price = _price.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _online = MutableStateFlow(Online)
    val online = _online.asStateFlow()
    
    private val _hidden = MutableStateFlow(Hidden)
    val hidden = _hidden.asStateFlow()
    
    private val _restrictChat = MutableStateFlow(RestrictChat)
    val restrictChat = _restrictChat.asStateFlow()
    
    private val _meetType = MutableStateFlow(
        MeetingType?.let { listOf(it.ordinal) } ?: emptyList()
    )
    val meetType = _meetType.asStateFlow()
    
    private val _condition = MutableStateFlow(
        Condition?.let { listOf(it.ordinal) } ?: emptyList()
    )
    val condition = _condition.asStateFlow()
    
    suspend fun changeOnline() {
        _online.emit(!online.value)
        Online = online.value
    }
    
    suspend fun changeHidden() {
        _hidden.emit(!hidden.value)
        Hidden = hidden.value
    }
    
    suspend fun changeMeetType(type: Int) {
        _meetType.emit(listOf(type))
        MeetingType = MeetType.get(type)
    }
    
    suspend fun changeCondition(condition: Int) {
        _condition.emit(listOf(condition))
        Condition = ConditionType.get(condition)
    }
    
    suspend fun changeRestrictChat() {
        _restrictChat.emit(!restrictChat.value)
        RestrictChat = restrictChat.value
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changePrice(text: String) {
        _price.emit(text)
        Price = text
    }
    
    suspend fun clearPrice() {
        _price.emit("")
        Price = ""
    }
}