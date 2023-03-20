package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.AgeFrom
import ru.rikmasters.gilty.addmeet.viewmodel.AgeTo
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager

class AgeBsViewModel(
    
    private val reqVm: RequirementsViewModel = RequirementsViewModel(),
): ViewModel() {
    
    private val manager by inject<MeetingManager>()
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _from = MutableStateFlow(
        AgeFrom.let { it.ifBlank { "18" } }
    )
    val from = _from.asStateFlow()
    
    private val _to = MutableStateFlow(
        AgeTo.let { it.ifBlank { "18" } }
    )
    val to = _to.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
            }
        }
    }
    
    suspend fun changeFrom(from: String) {
        if(to.value < from) _to.emit(from)
        _from.emit(from)
    }
    
    suspend fun changeTo(to: String) {
        if(from.value > to) _from.emit(to)
        _to.emit(to)
    }
    
    suspend fun onSave() {
        reqVm.selectAge(Pair(from.value, to.value))
    }
}