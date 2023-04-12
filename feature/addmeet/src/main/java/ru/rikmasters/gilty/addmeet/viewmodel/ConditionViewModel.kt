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

class ConditionViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    private val _meetType = MutableStateFlow(emptyList<Int>())
    val meetType = _meetType.asStateFlow()
    
    private val _condition = MutableStateFlow(emptyList<Int>())
    val condition = _condition.asStateFlow()
    
    private val _forbidden = MutableStateFlow(false)
    val forbidden = _forbidden.asStateFlow()
    
    private val _hidden = MutableStateFlow(false)
    val hidden = _hidden.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest { add ->
                _price.emit(add?.price ?: "")
                _online.emit(add?.isOnline ?: false)
                _forbidden.emit(add?.chatForbidden ?: false)
                _hidden.emit(add?.photoAccess ?: false)
                _condition.emit(add?.condition?.let {
                    if(it == ConditionType.NON_SELECT) emptyList()
                    else listOf(it.ordinal)
                } ?: emptyList())
                _meetType.emit(add?.type?.let {
                    if(it == MeetType.NON_SELECT) emptyList()
                    else listOf(it.ordinal)
                } ?: emptyList())
            }
        }
    }
    
    suspend fun changePrice(text: String) {
        _price.emit(text)
        manager.update(price = text)
    }
    
    suspend fun changeOnline(state: Boolean) {
        _online.emit(state)
        manager.update(isOnline = state)
    }
    
    suspend fun changeHidden(state: Boolean) {
        _hidden.emit(state)
        manager.update(photoAccess = state)
    }
    
    suspend fun changeMeetType(type: Int) {
        _meetType.emit(listOf(type))
        manager.update(type = MeetType.get(type))
    }
    
    suspend fun changeCondition(condition: Int) {
        _condition.emit(listOf(condition))
        manager.update(condition = ConditionType.get(condition))
    }
    
    suspend fun changeForbiddenChat(state: Boolean) {
        _forbidden.emit(state)
        manager.update(chatForbidden = state)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun clearAddMeet() {
        manager.clearAddMeet()
    }
}