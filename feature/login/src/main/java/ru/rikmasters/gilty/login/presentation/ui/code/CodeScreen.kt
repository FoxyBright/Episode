package ru.rikmasters.gilty.login.presentation.ui.code

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    
    val code by vm.code.collectAsState()
    val blur by vm.blur.collectAsState()
    val focuses by vm.focuses.collectAsState()
    val timer by vm.timer.collectAsState()
    
    LaunchedEffect(Unit) {
        Toast.makeText(
            context, "Ваш код: ${vm.getCode()}",
            Toast.LENGTH_LONG
        ).show()
        while(timer > 0) {
            delay(1000L)
            vm.onTimerChange(timer - 1)
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
                scope.launch { vm.onTimerChange(60) }
            }
            
            override fun onCodeChange(index: Int, text: String) {
                scope.launch {
                    vm.onCodeChange(index, text)
                    
                    val codeCheck = vm.code.value
                    
                    if(codeCheck.length == vm.codeLength.value)
                        if(codeCheck == vm.getCode()) {
                            vm.onOtpAuthentication(codeCheck)
                            if(vm.isUserRegistered())
                                nav.navigateAbsolute("main/meetings")
                            else
                                nav.navigate("profile")
                        } else {
                            asm.keyboard.hide()
                            vm.onBlur(true)
                        }
                }
            }
        })
}