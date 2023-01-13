package ru.rikmasters.gilty.profile.viewmodel

import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class SettingsViewModel: ViewModel() {
    
    private val authManager by inject<AuthManager>()
    
    suspend fun logout(){
        authManager.logout()
    }
    
}