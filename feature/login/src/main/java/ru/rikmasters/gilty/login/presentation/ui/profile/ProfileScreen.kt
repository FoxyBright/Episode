package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.shared.ProfileCallback
import ru.rikmasters.gilty.shared.shared.ProfileState

@Composable
fun ProfileScreen(nav: NavState = get()) {
    val lockState = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val profileState = ProfileState(
        name = name.value,
        lockState = lockState.value,
        description = description.value,
        enabled = true
    )
    ProfileContent(profileState, Modifier, object : ProfileCallback {
        override fun onNameChange(text: String) {
            name.value = text
        }

        override fun onLockClick(state: Boolean) {
            lockState.value = state
        }

        override fun onDescriptionChange(text: String) {
            description.value = text
        }

        override fun onBack() {
            nav.navigateAbsolute("authorization")
        }

        override fun onNext() {
            nav.navigate("personal")
        }
    })
}