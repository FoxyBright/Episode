package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun LoginScreen(nav: NavState = get()) {
    LoginContent(Modifier, object : LoginCallback {
        override fun onNext() {
            nav.navigate("registration/code")
        }
    })
}