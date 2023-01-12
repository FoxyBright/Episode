package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class AgeBsViewModel(
    
    private val personalVm: PersonalViewModel

): ViewModel() {
    
    private val _age = MutableStateFlow(18)
    val age = _age.asStateFlow()
    
    suspend fun changeAge(value: Int) {
        _age.emit(value)
    }
    
    suspend fun save() {
        personalVm.setAge(age.value)
    }
}