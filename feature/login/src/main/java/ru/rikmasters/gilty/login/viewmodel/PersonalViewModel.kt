package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import java.io.File

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
        UserGender = GenderType.get(index)
    }
    
    suspend fun setAge(it: Int) {
        _age.emit(it)
        UserAge = it
    }
    
    suspend fun updateProfile() {
        regManager.userUpdateData(
            UserName.ifBlank { null },
            UserDescription.ifBlank { null },
            UserAge,
            UserGender
        )
        try {
            regManager.setAvatar(File(Avatar), ListPoints)
        } catch(e: Exception) {
            logE(e.toString())
        }
        try {
            regManager.setHidden(ListHidden.map(::File))
        } catch(e: Exception) {
            logE(e.toString())
        }
    }
}