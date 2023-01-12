package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel


private var NOTIFICATION_PERMISSION = true

class PermissionViewModel: ViewModel() {
    
    private val _notification =
        MutableStateFlow(NOTIFICATION_PERMISSION)
    val notification = _notification.asStateFlow()
    
    suspend fun setNotificationPermission() {
        _notification.emit(!notification.value)
        NOTIFICATION_PERMISSION = notification.value
    }
}