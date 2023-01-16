package ru.rikmasters.gilty.login.viewmodel

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class CodeViewModel(
    
    private val repository: LoginRepository

): ViewModel() {
    
    private val authManager by inject<AuthManager>()
    private val regManager by inject<RegistrationManager>()
    
    private val _codeLength = MutableStateFlow(4)
    val codeLength = _codeLength.asStateFlow()
    
    private val _code = MutableStateFlow("")
    val code = _code.asStateFlow()
    
    private val _blur = MutableStateFlow(false)
    val blur = _blur.asStateFlow()
    
    private val _timer = MutableStateFlow(60)
    val timer = _timer.asStateFlow()
    
    private val _focuses = MutableStateFlow(lazy {
        val focuses = arrayListOf<FocusRequester>()
        repeat(codeLength.value) { focuses.add(FocusRequester()) }
        focuses
    }.value)
    val focuses = _focuses.asStateFlow()
    
    suspend fun updateSendCode() {
        
        authManager.getAuth().phone?.let { phone ->
            repository.sendCode(phone).let {
                authManager.updateAuth { copy(phone = phone, sendCode = it) }
            }
        }
        
        val senCode = authManager.getSendCode()
        
        senCode?.codeTimeout?.let { _timer.emit(it) }
        senCode?.codeLength?.let { _codeLength.emit(it) }
        
        makeToast("Ваш код: ${getCode()}")
    }
    
    suspend fun getCode() = authManager.getSendCode()?.code
    
    suspend fun link(token: String){
        authManager.linkExternal(token)
    }
    
    suspend fun onCodeClear() {
        _code.emit("")
        focuses.value[0].requestFocus()
    }
    
    suspend fun onCodeChange(index: Int, text: String) {
        if(code.value.length <= codeLength.value) {
            if(text.length == codeLength.value) {
                _code.emit(text)
            } else if(text.length < 2) {
                if(text == "") {
                    _code.emit(code.value.substring(0, code.value.lastIndex))
                    if(index - 1 >= 0)
                        focuses.value[index - 1].requestFocus()
                } else {
                    _code.emit(code.value + text)
                    if(index + 1 < codeLength.value)
                        focuses.value[index + 1].requestFocus()
                }
            }
        } else _code.emit("")
    }
    
    suspend fun onOtpAuthentication(code: String) {
        authManager.onOtpAuthentication(code)
    }
    
    suspend fun isUserRegistered(): Boolean =
        regManager.isUserRegistered()
    
    suspend fun onBlur(state: Boolean) {
        _blur.emit(state)
    }
    
    suspend fun onTimerChange(sec: Int) {
        _timer.emit(sec)
    }
}