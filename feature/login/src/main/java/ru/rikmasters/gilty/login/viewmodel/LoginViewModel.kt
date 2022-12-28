package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.auth.login.LoginMethod
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.core.util.random.randomAlphanumericString
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.country.Country
import ru.rikmasters.gilty.shared.country.CountryManager

class LoginViewModel(
    
    private val repository: LoginRepository,
    
    countryManager: CountryManager,

): ViewModel() {
    
    private val externalState = MutableStateFlow<String?>(null)
    
    private suspend fun generateExternalState(): String {
        randomAlphanumericString(32).let {
            externalState.emit(it)
            return it
        }
    }
    
    private val _country = MutableStateFlow(countryManager.defaultCountry)
    val country = _country.asStateFlow()
    
    suspend fun selectCountry(country: Country) {
        _country.emit(country)
        clearPhone()
    }
    
    private val _loginMethods = MutableStateFlow<Set<LoginMethod>>(emptySet())
    val loginMethods = _loginMethods.asStateFlow()
    
    suspend fun loadLoginMethods() = singleLoading {
        repository.getLoginMethods(generateExternalState()).let {
            _loginMethods.emit(it)
        }
    }
    
    suspend fun loginVia(method: LoginMethod) {
        // TODO
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
}