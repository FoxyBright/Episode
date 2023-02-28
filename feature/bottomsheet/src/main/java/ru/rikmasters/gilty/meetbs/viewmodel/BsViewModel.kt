package ru.rikmasters.gilty.meetbs.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class BsViewModel: ViewModel() {
    
    private val _alertState = MutableStateFlow(false)
    val alertState = _alertState.asStateFlow()
    
    suspend fun dismissAlertState(state: Boolean) {
        _alertState.emit(state)
    }
}