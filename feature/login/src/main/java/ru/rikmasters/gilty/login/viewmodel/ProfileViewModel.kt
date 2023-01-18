package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

var Avatar: String = ""
var Hidden: String = ""
var UserName: String = ""
var UserDescription: String = ""

class ProfileViewModel: ViewModel() {
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _username = MutableStateFlow(UserName)
    val username = _username.asStateFlow()
    
    private val _description = MutableStateFlow(UserDescription)
    val description = _description.asStateFlow()
    
    suspend fun usernameChange(text: String) {
        _username.emit(text)
        UserName = text
        _occupied.emit(checkUsernameOccupied())
    }
    
    private fun checkUsernameOccupied(): Boolean {
        return username.value == "qwerty" //TODO Написать запрос-проверку на наличие имени
    }
    
    suspend fun descriptionChange(text: String) {
        _description.emit(text)
        UserDescription = text
    }
}