package ru.rikmasters.gilty.profile.viewmodel.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class SettingsViewModel: ViewModel() {
    
    private val authManager by inject<AuthManager>()
    private val profileManager by inject<ProfileManager>()
    private val meetManager by inject<MeetingManager>()
    
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
    
    suspend fun changeOrientation(orientation: OrientationModel) = singleLoading {
        profileManager.userUpdateData(orientation = orientation)
        _orientation.emit(orientation)
    }
    
    suspend fun changeGender(gender: GenderType) = singleLoading {
        profileManager.userUpdateData(gender = gender)
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
        profileManager.userUpdateData(age = age)
        _age.emit("$age")
    }
    
    suspend fun getUserData(
        gender: String,
        age: String,
        orientation: Pair<String, String>,
        phone: String,
    ) = singleLoading {
        _gender.emit(GenderType.valueOf(gender))
        _age.emit(age)
        _orientation.emit(
            OrientationModel(
                orientation.first,
                orientation.second
            )
        )
        _phone.emit(phone)
    }
    
    suspend fun getOrientations() = singleLoading {
        _orientations.emit(meetManager.getOrientations())
    }
    
    suspend fun deleteAccount() = singleLoading {
        profileManager.deleteAccount()
        authManager.logout()
        makeToast("Ваш аккаунт был удален!")
    }
    
    suspend fun changePhone() = singleLoading {}
    
    suspend fun logout() = singleLoading {
        authManager.logout()
    }
}