package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.core.util.random.randomAlphanumericString
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class LoginViewModel(

    private val repository: LoginRepository

): ViewModel() {
    
    private val externalState = MutableStateFlow<String?>(null)
    
    private suspend fun generateExternalState(): String {
        randomAlphanumericString(32).let {
            externalState.emit(it)
            return it
        }
    }
    
    private val _loginMethods = MutableStateFlow<Set<LoginMethod>>(emptySet())
    val loginMethods = _loginMethods.asStateFlow()
    
    suspend fun loadLoginMethods() = singleLoading {
        repository.getLoginMethods(generateExternalState()).let {
            _loginMethods.emit(it)
        }
    }
}