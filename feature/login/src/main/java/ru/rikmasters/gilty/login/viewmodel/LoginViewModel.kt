package ru.rikmasters.gilty.login.viewmodel

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.CountryManager

class LoginViewModel(
    
    private val repository: LoginRepository,
    
    countryManager: CountryManager

): ViewModel() {
    
    private val authManager by inject<AuthManager>()
    
    private val _country = MutableStateFlow(countryManager.defaultCountry)
    val country = _country.asStateFlow()
    
    suspend fun selectCountry(country: Country) {
        _country.emit(country)
        clearPhone()
    }
    
    private val _loginMethods = MutableStateFlow<Set<LoginMethod>>(emptySet())
    val loginMethods = _loginMethods.asStateFlow()
    
    suspend fun loadLoginMethods() = singleLoading {
        repository.getLoginMethods(authManager.getAuth().externalState).let {
            _loginMethods.emit(it)
        }
    }
    
    private val _phone = MutableStateFlow(country.value.clearPhoneDial)
    val phone = _phone.asStateFlow()
    
    suspend fun changePhone(text: String) {
        if(text.length >= phone.value.length && !text.startsWith(country.value.clearPhoneDial))
            _phone.emit(country.value.clearPhoneDial.substring(0, text.length))
        else
            _phone.emit(text)
    }
    
    suspend fun clearPhone() {
        _phone.emit(country.value.clearPhoneDial)
    }
    
    suspend fun handle(deepLink: Uri): Boolean {
        if(deepLink.host != "external") return false
        if(deepLink.getQueryParameter("state") !=
            authManager.getAuth().externalState
        ) return false
        
        deepLink.getQueryParameter("token")?.let {
            if(authManager.isExternalLinked(it)) makeToast("Привязан")
            else makeToast("Не привязан")
            return true
        }
        
        return false
    }
    
    suspend fun sendCode() =
        repository.sendCode(_phone.value).let { // TODO Сделать механизм саги
            authManager.updateAuth { copy(phone = _phone.value, sendCode = it) }
        }
}