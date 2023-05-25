package ru.rikmasters.gilty.profile.viewmodel.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.chats.manager.ChatManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class SettingsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    private val chatManager by inject<ChatManager>()
    private val authManager by inject<AuthManager>()
    
    private val context = getKoin().get<Context>()
    
    private val _exitAlert = MutableStateFlow(false)
    val exitAlert = _exitAlert.asStateFlow()
    
    private val _deleteAlert = MutableStateFlow(false)
    val deleteAlert = _deleteAlert.asStateFlow()
    
    private val _orientation = MutableStateFlow<OrientationModel?>(null)
    val orientation = _orientation.asStateFlow()
    
    private val _gender = MutableStateFlow<GenderType?>(null)
    val gender = _gender.asStateFlow()
    
    private val _notifications = MutableStateFlow(false)
    val notifications = _notifications.asStateFlow()
    
    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()
    
    private val _age = MutableStateFlow("")
    val age = _age.asStateFlow()
    
    private val _orientations =
        MutableStateFlow<List<OrientationModel>?>(null)
    val orientations = _orientations.asStateFlow()
    
    suspend fun changeOrientation(orientation: OrientationModel) =
        singleLoading {
            profileManager.userUpdateData(
                orientation = orientation
            ).on(
                success = {},
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
            _orientation.emit(orientation)
        }
    
    suspend fun changeGender(gender: GenderType) = singleLoading {
        profileManager.userUpdateData(
            gender = gender
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
        _gender.emit(gender)
    }
    
    suspend fun exitAlertDismiss(state: Boolean) {
        _exitAlert.emit(state)
    }
    
    suspend fun deleteAlertDismiss(state: Boolean) {
        _deleteAlert.emit(state)
    }
    
    suspend fun setNotification(state: Boolean) {
        _notifications.emit(state)
    }
    
    suspend fun changeAge(age: Int) = singleLoading {
        profileManager.userUpdateData(
            age = age
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
        _age.emit("$age")
    }
    
    suspend fun getUserData() = singleLoading {
        val profile = profileManager
            .getProfile(false)
        
        _gender.emit(profile.gender)
        _age.emit("${profile.age}")
        _orientation.emit(profile.orientation)
        _phone.emit("${profile.phone}")
    }
    
    suspend fun getOrientations() = singleLoading {
        meetManager.getOrientations().on(
            success = { _orientations.emit(it) },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun deleteAccount() = singleLoading {
        profileManager.deleteAccount().on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
        chatManager.disconnectWebSocket()
        authManager.logout()
        makeToast("Ваш аккаунт был удален!")
    }
    
    suspend fun logout() = singleLoading {
        profileManager.clearProfile()
        chatManager.disconnectWebSocket()
        authManager.logout()
        makeToast("До новых Meet!")
    }
}