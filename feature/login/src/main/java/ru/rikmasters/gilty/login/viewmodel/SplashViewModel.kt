package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class SplashViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    private val authManager by inject<AuthManager>()
    
    private val _screen = MutableStateFlow("")
    val screen = _screen.asStateFlow()
    
    suspend fun getScreen() {
        _screen.emit(
            regManager.storageProfile()
                ?.let { "main/meetings" }
                ?: if(
                    authManager.hasTokens() &&
                    regManager.profileCompleted()
                ) "main/meetings"
                else "login"
        )
    }
}