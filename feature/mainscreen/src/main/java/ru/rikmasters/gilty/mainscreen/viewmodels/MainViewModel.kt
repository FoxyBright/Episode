package ru.rikmasters.gilty.mainscreen.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class MainViewModel: ViewModel() {
    
    private val _today = MutableStateFlow(true)
    val today = _today.asStateFlow()
    
    suspend fun changeGroup() {
        _today.emit(!today.value)
    }
}