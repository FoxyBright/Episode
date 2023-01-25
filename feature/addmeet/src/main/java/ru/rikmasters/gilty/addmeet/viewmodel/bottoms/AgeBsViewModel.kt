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
    
    private val _from = MutableStateFlow(AgeFrom)
    val from = _from.asStateFlow()
    
    private val _to = MutableStateFlow(AgeTo)
    val to = _to.asStateFlow()
    
    suspend fun changeFrom(from: String) {
        _from.emit(from)
    }
    
    suspend fun changeTo(to: String) {
        _to.emit(to)
    }
    
    suspend fun onSave() {
        reqVm.selectAge(Pair(from.value, to.value))
    }
}