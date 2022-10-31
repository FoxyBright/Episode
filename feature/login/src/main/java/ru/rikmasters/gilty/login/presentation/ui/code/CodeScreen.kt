package ru.rikmasters.gilty.login.presentation.ui.code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun CodeScreen(nav: NavState = get()) {
    var text by remember { mutableStateOf("") }
    val blur = remember { mutableStateOf(false) }
    val focuses = remember { Array(4) { FocusRequester() } }
    var sec by remember { mutableStateOf(60) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L); sec--
        }
    }
    CodeContent(CodeState(text, focuses, sec, blur.value), Modifier, object : CodeCallback {
        override fun onBack() {
            nav.navigateAbsolute("authorization")
        }

        override fun onBlur() {
            blur.value = false
        }

        override fun onCodeSend() {
            sec = 60
        }

        override fun onCodeChange(index: Int, it: String) {
            if (text.length <= focuses.size) {
                if (it.length == focuses.size) {
                    text = it
                } else if (it.length < 2) {
                    if (it == "") {
                        text = text.substring(0, text.lastIndex)
                        if (index - 1 >= 0)
                            focuses[index - 1].requestFocus()
                    } else {
                        text += it
                        if (index + 1 < focuses.size)
                            focuses[index + 1].requestFocus()
                    }
                }
            } else text = ""
            if (text.length == focuses.size)
                if (text == "1234") nav.navigate("profile")
                else blur.value = true
        }
    })
}