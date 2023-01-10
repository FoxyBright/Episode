package ru.rikmasters.gilty.login.viewmodel

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class CodeViewModel: ViewModel() {
    
    private val _codeLength = MutableStateFlow(4)
    val codeLength = _codeLength.asStateFlow()
    
    private val _correctCode = MutableStateFlow("0000")
    val correctCode = _correctCode.asStateFlow()
    
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
    
    suspend fun onBlur(state: Boolean) {
        _blur.emit(state)
    }
    
    suspend fun onTimerChange(sec: Int) {
        _timer.emit(sec)
    }
    
}