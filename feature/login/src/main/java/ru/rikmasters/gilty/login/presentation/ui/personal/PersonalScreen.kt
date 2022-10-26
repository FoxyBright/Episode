package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.presentation.ui.PersonalInfoContent
import ru.rikmasters.gilty.shared.NavigationInterface

@Composable
fun PersonalScreen(nav: NavState = get()) {
    PersonalInfoContent(object : NavigationInterface {
        override fun onBack() {
            nav.navigate("profile")
        }

        override fun onNext() {
            nav.navigate("categories")
        }
    })
}