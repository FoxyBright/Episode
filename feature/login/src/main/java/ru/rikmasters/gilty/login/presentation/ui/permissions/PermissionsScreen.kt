package ru.rikmasters.gilty.login.presentation.ui.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.presentation.ui.PermissionsContent
import ru.rikmasters.gilty.shared.NavigationInterface

@Composable
fun PermissionsScreen(nav: NavState = get()) {
    PermissionsContent(Modifier, object : NavigationInterface {
        override fun onBack() {
            nav.navigate("categories")
        }

        override fun onNext() {
            nav.navigateAbsolute("main/meetings")
        }
    })
}