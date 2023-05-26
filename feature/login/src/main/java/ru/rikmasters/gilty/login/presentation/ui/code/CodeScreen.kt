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
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val focuses by vm.focuses.collectAsState()
    val blur by vm.blur.collectAsState()
    val code by vm.code.collectAsState()
    val timer by vm.timer.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.firstFocus()
        while(true) {
            delay(1000L)
            vm.onTimerChange()
        }
    }
    
    CodeContent(
        CodeState(code, focuses, timer, blur),
        Modifier, object: CodeCallback {
            
            override fun onCodeChange(index: Int, text: String) {
                scope.launch {
                    vm.onCodeChange(index, text)
                    
                    if(vm.code.value.length == vm.codeLength.value) try {

                        if(vm.onOtpAuthentication(vm.code.value)) {

                            vm.linkExternalToken()

                            if (vm.profileCompleted())
                                nav.clearStackNavigation("main/meetings")
                            else
                                nav.clearStackNavigation("profile")
                        }else {
                            badCode()
                        }
                    } catch(e: Exception) {
                        e.stackTraceToString()
                        badCode()
                    }
                }
            }
            
            private suspend fun badCode() {
                scope.launch {
                    asm.keyboard.hide()
                    vm.onBlur(true)
                    vm.onCodeClear()
                }
            }
            
            override fun onBlur() {
                scope.launch {
                    vm.onCodeClear()
                    vm.onBlur(false)
                }
            }
            
            override fun onCodeSend() {
                scope.launch { vm.updateSendCode() }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
        }
    )
}