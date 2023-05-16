package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType

class PersonalViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val _age = MutableStateFlow<Int?>(null)
    val age = _age.asStateFlow()
    
    private val _gender = MutableStateFlow<Int?>(null)
    val gender = _gender.asStateFlow()
    
    suspend fun setGender(index: Int) = singleLoading {
        _gender.emit(index)
        regManager.userUpdateData(
            gender = GenderType.get(index)
        )
    }
    
    suspend fun setAge(age: Int) = singleLoading {
        _age.emit(age)
        regManager.userUpdateData(
            age = age
        )
    }
}