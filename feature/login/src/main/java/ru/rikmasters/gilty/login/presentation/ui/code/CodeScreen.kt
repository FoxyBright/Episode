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
    
    val timer by vm.timer.collectAsState()
    
    LaunchedEffect(Unit) {
        while(timer > 0) {
            delay(1000L)
            vm.onTimerChange(timer - 1)
        }
    }
    
    val code by vm.code.collectAsState()
    val blur by vm.blur.collectAsState()
    val focuses by vm.focuses.collectAsState()
    
    CodeContent(
        CodeState(code, focuses, timer, blur),
        Modifier, object: CodeCallback {
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onBlur() {
                scope.launch {
                    vm.onBlur(false)
                    nav.navigate("profile")
                }
            }
            
            override fun onCodeSend() {
                scope.launch { vm.onTimerChange(60) }
            }
            
            override fun onCodeChange(index: Int, text: String) {
                scope.launch {
                    vm.onCodeChange(index, text)
                    val codeCheck = vm.code.value
                    if(codeCheck.length == vm.codeLength.value)
                        if(codeCheck == vm.correctCode.value)
                            nav.navigate("profile")
                        else {
                            asm.keyboard.hide()
                            vm.onBlur(true)
                        }
                }
            }
        })
}