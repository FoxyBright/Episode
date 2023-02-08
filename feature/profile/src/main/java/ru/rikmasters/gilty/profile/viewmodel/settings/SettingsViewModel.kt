package ru.rikmasters.gilty.profile.viewmodel.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class SettingsViewModel: ViewModel() {
    
    private val authManager by inject<AuthManager>()
    private val profileManager by inject<ProfileManager>()
    
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
    
    suspend fun setNotification() {
        _notifications.emit(!notifications.value)
    }
    
    suspend fun changeAge(age: Int) = singleLoading {
        profileManager.userUpdateData(age = age)
        _age.emit("$age")
    }
    
    suspend fun getUserData() = singleLoading {
        val user = profileManager.getProfile()
        _gender.emit(user.gender)
        _age.emit(user.age.toString())
        _orientation.emit(user.orientation)
        _phone.emit(user.phone ?: "")
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