package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel

var Avatar: String = ""
var ListPoints: List<Int> = listOf()
var Hidden: String = ""
var UserName: String = ""
var UserDescription: String = ""

class ProfileViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    private val authManager by inject<AuthManager>()
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _username = MutableStateFlow(UserName)
    val username = _username.asStateFlow()
    
    private val _description = MutableStateFlow(UserDescription)
    val description = _description.asStateFlow()
    
    suspend fun usernameChange(text: String) {
        _username.emit(text)
        UserName = text
        _occupied.emit(
            regManager.isNameOccupied(username.value)
        )
    }
    
    suspend fun descriptionChange(text: String) {
        _description.emit(text)
        UserDescription = text
    }
}