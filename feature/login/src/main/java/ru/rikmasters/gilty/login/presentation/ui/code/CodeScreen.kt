package ru.rikmasters.gilty.login.presentation.ui.code

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.CodeViewModel

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CodeScreen(vm: CodeViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val code by vm.code.collectAsState()
    val blur by vm.blur.collectAsState()
    val focuses by vm.focuses.collectAsState()
    val timer by vm.timer.collectAsState()
    
    LaunchedEffect(Unit) {
        while(true) { // TODO Пока единственный рабочий метод постоянной работы таймера - исправить
            delay(1000L)
            vm.onTimerChange()
        }
    }
    
    CodeContent(
        CodeState(code, focuses, timer, blur),
        Modifier, object: CodeCallback {
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onBlur() {
                scope.launch {
                    vm.onCodeClear()
                    vm.onBlur(false)
                }
            }
            
            override fun onCodeSend() {
                scope.launch {
                    vm.updateSendCode()
                }
            }
            
            override fun onCodeChange(index: Int, text: String) {
                scope.launch {
                    vm.onCodeChange(index, text)
        
                    if(vm.code.value.length == vm.codeLength.value) {
                        try {
                            vm.onOtpAuthentication(vm.code.value)
                            vm.linkExternalToken()
                            if(vm.isUserRegistered())
                                nav.navigateAbsolute("main/meetings")
                            else
                                nav.navigate("profile")
                        } catch(_: Exception) {
                            badCode()
                        }
                    }
                }
            }
            
            private suspend fun badCode() {
                scope.launch {
                    asm.keyboard.hide()
                    vm.onBlur(true)
                }
            }
        })
}