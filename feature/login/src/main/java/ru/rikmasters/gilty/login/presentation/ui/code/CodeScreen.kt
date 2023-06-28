package ru.rikmasters.gilty.login.presentation.ui.code

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
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

    Use<CodeViewModel>(LoadingTrait) {
        CodeContent(
            state = CodeState(
                code = code,
                focuses = focuses,
                sec = timer,
                blurErrorMessage = blur),
            modifier = Modifier.systemBarsPadding(), callback = object : CodeCallback {

                override fun onCodeChange(index: Int, text: String) {
                    if(blur != null || vm.loading.value) return
                    scope.launch {
                        vm.onCodeChange(index, text)
                        if (vm.code.value.length == vm.codeLength.value) try {
                            val errorMessageOnAuth = vm.onOtpAuthentication(vm.code.value)
                            if (errorMessageOnAuth == null) {
                                val linkExternalTokenError = vm.linkExternalToken()
                                if(linkExternalTokenError != null) {
                                    badCode(linkExternalTokenError)
                                    return@launch
                                }

                                if (vm.profileCompleted())
                                    nav.clearStackNavigationAbsolute("main/meetings")
                                else
                                    nav.navigate("profile")
                            } else badCode(errorMessageOnAuth)
                        } catch (e: Exception) {
                            e.stackTraceToString()
                            badCode(e.message.toString())
                        }
                    }
                }

                private suspend fun badCode(error:String) {
                    scope.launch {
                        asm.keyboard.hide()
                        vm.onBlur(error)
                        vm.onCodeClear()
                    }
                }

                override fun onBlur() {
                    scope.launch {
                        vm.onCodeClear()
                        vm.onBlur(null)
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
}