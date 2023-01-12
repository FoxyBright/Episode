package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class PersonalViewModel: ViewModel() {
    
    private val _age = MutableStateFlow<Int?>(null)
    val age = _age.asStateFlow()
    
    private val _gender = MutableStateFlow<Int?>(null)
    val gender = _gender.asStateFlow()
    
    suspend fun setGender(index: Int) {
        _gender.emit(index)
    }
    
    suspend fun setAge(it: Int) {
        log.d(it.toString())
        _age.emit(it) // TODO При перезапуске окна не работает
    }
    
    fun updateProfile(){
    
    }
}