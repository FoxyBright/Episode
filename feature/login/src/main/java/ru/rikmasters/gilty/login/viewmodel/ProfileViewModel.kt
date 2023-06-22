package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class ProfileViewModel : ViewModel() {

    private val regManager by inject<RegistrationManager>()
    private val authManager by inject<AuthManager>()

    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val usernameDebounced = username
        .debounce(250)
        .onEach { name ->
            if(_profile.value != null)
                onUsernameSave()
        /*    val occupied =
                if (name == profile.value?.username) false
                else if (name.isNotEmpty())
                    regManager.isNameOccupied(name).on(
                        success = { it },
                        loading = { false },
                        error = {
                            if (it.serverMessage == "errors.user.username.exists") false
                            else {
                                _errorMessage.emit(it.serverMessage ?: "")
                                false
                            }
                        }
                    )
                else false
            _occupied.emit(occupied)*/
        }
        .state(_username.value, Eagerly)

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()


    suspend fun getProfile() = singleLoading {

        regManager.deleteHiddenPhotosAmount()

        val profile = regManager.getProfile(_profile.value != null)
        _profile.emit(profile)
        if (username.value.isEmpty())
            _username.emit(profile.username ?: "")
        if (description.value.isEmpty())
            _description.emit(profile.aboutMe ?: "")
    }

    suspend fun clearLoginData() {
        regManager.clearProfile()
        authManager.logout()
    }

    suspend fun usernameChange(text: String) {
        _errorMessage.emit("")
        _username.emit(text)
    }

    suspend fun descriptionChange(text: String) {
        if (text.length <= 120)
            _description.emit(text)
    }

    private suspend fun onUsernameSave() {
        regManager.userUpdateData(
            username = username.value,
            aboutMe = description.value
        ).on(
            success = {},
            loading = {},
            error = {
                _errorMessage.emit(it.serverMessage.toString())
            }
        )
    }

    suspend fun checkOnNext() = singleLoading {
        regManager
            .getProfile(true)
            .let {
                if (it.username.isNullOrBlank())
                    onUsernameSave()
            }
    }
}