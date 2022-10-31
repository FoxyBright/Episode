package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState

@Composable
fun ProfileScreen(
    photo: String,
    hiddenPhoto: String,
    nav: NavState = get()
) {
    val occupiedName = remember { mutableStateOf(false) }
    val lockState = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val profileState = ProfileState(
        profilePhoto = photo,
        hiddenPhoto = hiddenPhoto,
        name = name.value,
        lockState = lockState.value,
        description = description.value,
        enabled = true,
        occupiedName = occupiedName.value
    )
    ProfileContent(profileState, Modifier, object : ProfileCallback {
        override fun onNameChange(text: String) {
            name.value = text
            occupiedName.value = name.value == "qwerty"
        }

        override fun onLockClick(state: Boolean) {
            lockState.value = state
        }

        override fun profileImage() {
            nav.navigateAbsolute("registration/avatar")
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