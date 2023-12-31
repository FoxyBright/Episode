package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

class ProfileViewModel : ViewModel() {

    private val regManager by inject<RegistrationManager>()
    private val authManager by inject<AuthManager>()

    private val context = getKoin().get<Context>()

    private val _occupied = MutableStateFlow(false)
    val occupied = _occupied.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()



    @Suppress("unused")
    @OptIn(FlowPreview::class)
    val usernameDebounced = username
        .debounce(250)
        .onEach { name ->
            val occupied =
                if (name == profile.value?.username) false
                else if(name.isNotEmpty())regManager.isNameOccupied(name).on(
                    success = { it },
                    loading = { false },
                    error = {
                        it.serverMessage ==
                                "errors.user.username.exists"
                    }
                )
                else false
            _occupied.emit(occupied)
        }
        .state(_username.value, Eagerly)

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()

    suspend fun getProfile() = singleLoading {

        regManager.deleteHiddenPhotosAmount()

        val profile = regManager.getProfile(true)
        _profile.emit(profile)
        if(username.value.isEmpty())
            _username.emit(profile.username ?: "")
        if(description.value.isEmpty())
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
        if (text.length <= 120)
            _description.emit(text)
    }

    private suspend fun onUsernameSave(onSuccess:()->Unit) {
        regManager.userUpdateData(
            username = username.value,
            aboutMe = description.value
        ).on(
            success = {onSuccess()},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    suspend fun checkOnNext(onSuccess:()->Unit) {
        regManager
            .getProfile(true)
            .let {
                if (it.username.isNullOrBlank())
                    onUsernameSave(onSuccess = { onSuccess() })
            }
    }
}