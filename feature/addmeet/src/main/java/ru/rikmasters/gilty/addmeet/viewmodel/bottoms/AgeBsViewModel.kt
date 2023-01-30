package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.addmeet.viewmodel.AgeFrom
import ru.rikmasters.gilty.addmeet.viewmodel.AgeTo
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class AgeBsViewModel(
    
    private val reqVm: RequirementsViewModel = RequirementsViewModel(),
): ViewModel() {
    
    private val _from = MutableStateFlow(
        AgeFrom.let { it.ifBlank { "18" } }
    )
    val from = _from.asStateFlow()
    
    private val _to = MutableStateFlow(
        AgeTo.let { it.ifBlank { "18" } }
    )
    val to = _to.asStateFlow()
    
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