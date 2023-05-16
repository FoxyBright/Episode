package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class ProfileViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    private val authManager by inject<AuthManager>()
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val usernameDebounced = username
        .debounce(250)
        .onEach { name ->
            _occupied.emit(
                if(name == profile.value?.username) false
                else regManager.isNameOccupied(name)
            )
        }
        .state(_username.value, Eagerly)
    
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()
    
    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()
    
    suspend fun getProfile() = singleLoading {
        val profile = regManager.getProfile()
        _profile.emit(profile)
        _username.emit(profile.username ?: "")
        _description.emit(profile.aboutMe ?: "")
    }
    
    suspend fun clearLoginData() {
        regManager.clearProfile()
        authManager.logout()
    }
    
    suspend fun usernameChange(text: String) {
        _username.emit(text)
    }
    
    suspend fun descriptionChange(text: String) {
        if(text.length <= 120)
            _description.emit(text)
    }
    
    suspend fun onDescriptionSave() {
        regManager.userUpdateData(
            aboutMe = description.value
        )
    }
    
    suspend fun onUsernameSave() {
        regManager.userUpdateData(
            username = username.value
        )
    }
    
    suspend fun checkOnNext() {
        regManager
            .getProfile(true)
            .let {
                if(it.username.isNullOrBlank())
                    onUsernameSave()
            }
    }
}