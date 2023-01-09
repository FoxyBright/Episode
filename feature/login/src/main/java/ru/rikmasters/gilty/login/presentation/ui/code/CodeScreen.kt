package ru.rikmasters.gilty.login.presentation.ui.code

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CodeScreen(nav: NavState = get()) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val codeSize = 4
    var text by remember { mutableStateOf("") }
    val blur = remember { mutableStateOf(false) }
    val focusList = arrayListOf<FocusRequester>()
    repeat(codeSize) { focusList.add(FocusRequester()) }
    val focuses by remember { mutableStateOf(focusList) }
    var sec by remember { mutableStateOf(60) }
    LaunchedEffect(Unit) {
        while(sec > 0) {
            delay(1000L); sec--
        }
    }
    CodeContent(CodeState(text, focuses, sec, blur.value), Modifier, object: CodeCallback {
        override fun onBack() {
            nav.navigationBack()
        }
        
        override fun onBlur() {
            nav.navigate("profile")
            /*blur.value = false*/
        }
        
        override fun onCodeSend() {
            sec = 60
        }
        
        override fun onCodeChange(index: Int, it: String) {
            if(text.length <= focuses.size) {
                if(it.length == focuses.size) {
                    text = it
                } else if(it.length < 2) {
                    if(it == "") {
                        text = text.substring(0, text.lastIndex)
                        if(index - 1 >= 0)
                            focuses[index - 1].requestFocus()
                    } else {
                        text += it
                        if(index + 1 < focuses.size)
                            focuses[index + 1].requestFocus()
                    }
                }
            } else text = ""
            if(text.length == codeSize)
                if(text == "code") nav.navigate("profile")
                else {
                    scope.launch { asm.keyboard.hide() }
                    blur.value = true
                }
        }
    })
}