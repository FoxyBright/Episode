package ru.rikmasters.gilty.login.presentation.ui.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun PermissionsScreen(nav: NavState = get()) {
    var geopositionState by remember { mutableStateOf(true) }
    var notificationState by remember { mutableStateOf(false) }
    val state = PermissionsState(geopositionState, notificationState)
    PermissionsContent(state, Modifier, object : PermissionsCallback {
        override fun onBack() {
            nav.navigationBack()
        }

        override fun geopositionChange() {
            geopositionState = !geopositionState
        }

        override fun notificationChange() {
            notificationState = !notificationState
        }

        override fun onNext() {
            nav.navigateAbsolute("main/meetings")
        }
    })
}