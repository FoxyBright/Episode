package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class PermissionViewModel: ViewModel() {
    
    private val _notification = MutableStateFlow(false)
    val notification = _notification.asStateFlow()
    
    suspend fun setNotificationPermission(state: Boolean) {
        _notification.emit(state)
    }
}