package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.GenderType
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.viewmodel.ViewModel

var UserAge: Int? = null
var UserGender: GenderType? = null

class PersonalViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val _age = MutableStateFlow(UserAge)
    val age = _age.asStateFlow()
    
    private val _gender = MutableStateFlow(UserGender?.ordinal)
    val gender = _gender.asStateFlow()
    
    suspend fun setGender(index: Int) {
        _gender.emit(index)
        UserGender = GenderType.valueOf(index)
    }
    
    suspend fun setAge(it: Int) {
        log.d(it.toString())
        _age.emit(it) // TODO При перезапуске окна не работает
        UserAge = it
    }
    
    suspend fun updateProfile() {
        
        val username = UserName.ifBlank { null }
        
        val description = UserDescription.ifBlank { null }
        
        regManager.userUpdateData(username, description, UserAge, UserGender)
    }
}