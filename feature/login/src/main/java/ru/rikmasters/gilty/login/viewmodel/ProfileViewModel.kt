package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class ProfileViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()
    
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    
    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val distanceDebounced = username
        .debounce(250)
        .onEach {
            _occupied.emit(
                if(username.value == profile.value?.username) false
                else regManager.isNameOccupied(username.value)
            )
        }
        .state(
            _username.value,
            SharingStarted.Eagerly
        )
    
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
    
    suspend fun usernameChange(text: String) {
        _username.emit(text)
    }
    
    suspend fun descriptionChange(text: String) {
        _description.emit(text)
    }
    
    suspend fun onDescriptionSave() {
        regManager.userUpdateData(
            description.value
        )
    }
    
    suspend fun onUsernameSave() {
        regManager.userUpdateData(
            username.value
        )
    }
}