package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.auth.login.SendCode
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.login.LoginErrorMessage
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.CountryManager

class LoginViewModel(
    
    private val repository: LoginRepository,
    countryManager: CountryManager,
): ViewModel() {
    
    private val authManager by inject<AuthManager>()
    private val context = getKoin().get<Context>()
    
    private val _country = MutableStateFlow(countryManager.defaultCountry)
    val country = _country.asStateFlow()
    
    suspend fun selectCountry(country: Country) {
        _country.emit(country)
        clearPhone()
    }
    
    private val _loginMethods = MutableStateFlow<Set<LoginMethod>>(emptySet())
    val loginMethods = _loginMethods.asStateFlow()
    
    private val _externalLogin = MutableStateFlow(false)
    val externalLogin = _externalLogin.asStateFlow()
    
    private val _loginMethod = MutableStateFlow("")
    val loginMethod = _loginMethod.asStateFlow()
    suspend fun changeLoginScreen() {
        _externalLogin.emit(!externalLogin.value)
    }
    
    suspend fun setLoginMethod(method: String) {
        _loginMethod.emit(method)
    }
    
    suspend fun loadLoginMethods() = singleLoading {
        repository.getLoginMethods(
            authManager.getAuth().externalState
        ).let { _loginMethods.emit(it) }
    }
    
    private val _phone = MutableStateFlow(country.value.clearPhoneDial)
    val phone = _phone.asStateFlow()
    
    suspend fun getSendCode(): SendCode? =
        authManager.getSendCode()
    
    suspend fun changePhone(text: String) {
        val dial = country.value.clearPhoneDial
        val length = text.length
        
        _phone.emit(
            if(length >= phone.value.length &&
                !text.startsWith(dial)
            ) dial.substring(0, length)
            else text
        )
    }
    
    suspend fun clearPhone() {
        _phone.emit(country.value.clearPhoneDial)
    }
    
    suspend fun handle(
        deepLink: Uri,
    ): Pair<Boolean, Boolean> {
        
        if(deepLink.host != "external")
            return Pair(false, false)
        
        if(deepLink.getQueryParameter("state") !=
            authManager.getAuth().externalState
        ) return Pair(false, false)
        
        deepLink.getQueryParameter("token")?.let {
            authManager.updateAuth { copy(externalToken = it) }
            
            if(authManager.isExternalLinked(it))
                return Pair(true, true)
            else
                return Pair(true, false)
        }
        
        return Pair(false, false)
    }
    
    suspend fun sendCode() {
        repository.sendCode(_phone.value)
            .let { (sendCode, message) ->
                
                if(sendCode == null)
                    message?.let {
                        makeToast(
                            LoginErrorMessage(it, context).message
                        )
                    }
                
                authManager.updateAuth {
                    copy(
                        phone = _phone.value,
                        sendCode = sendCode
                    )
                }
            }
    }
}