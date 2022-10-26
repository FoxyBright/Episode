package ru.rikmasters.gilty.login.presentation.ui.code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun CodeScreen(nav: NavState = get()) {
    var text by remember { mutableStateOf("") }
    val focuses = remember { Array(4) { FocusRequester() } }
    CodeContent(CodeState(text, focuses), Modifier, object : CodeCallback {
        override fun onBack() {
            nav.navigateAbsolute("authorization")
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
            if (text.length == focuses.size) nav.navigate("profile")
        }
    })
}