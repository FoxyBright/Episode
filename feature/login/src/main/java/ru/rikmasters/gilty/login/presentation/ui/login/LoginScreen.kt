package ru.rikmasters.gilty.login.presentation.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.presentation.model.Countries
import ru.rikmasters.gilty.login.presentation.model.Country
import ru.rikmasters.gilty.login.presentation.model.DemoCountry

@Composable
fun LoginScreen(nav: NavState = get()) {
    var phone by remember { mutableStateOf("") }
    var selectCountry by remember { mutableStateOf(DemoCountry) }
    LoginContent(LoginState(
        rememberCoroutineScope(),
        phone, selectCountry, Countries()
    ),
        Modifier, object : LoginCallback {
            override fun onPhoneChange(text: String) {
                phone = text
            }

            override fun onCountryChange(country: Country) {
                selectCountry = country
            }

            override fun onNext() {
                nav.navigate("registration/code")
            }
        })
}