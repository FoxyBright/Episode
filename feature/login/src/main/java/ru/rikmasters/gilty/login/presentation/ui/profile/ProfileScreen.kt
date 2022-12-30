package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.profile.EmojiList

@Composable
fun ProfileScreen(
    photo: String = "",
    hiddenPhoto: String = "",
    nav: NavState = get()
) {
    val occupiedName = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf(UserName) }
    val description = remember { mutableStateOf(UserDescription) }
    val profileState = ProfileState(
        name.value, if (hiddenPhoto == "") Hidden else hiddenPhoto,
        if (photo == "") Avatar else photo,
        description.value, "0.0",
        emoji = EmojiList.first(),
        enabled = true,
        occupiedName = occupiedName.value
    )
    ProfileContent(profileState, Modifier, object : ProfileCallback {
        override fun onNameChange(text: String) {
            UserName = text
            name.value = text
            occupiedName.value = name.value == "qwerty"
        }
        
        override fun profileImage() {
            nav.navigateAbsolute("registration/gallery?multi=false")
        }

        override fun hiddenImages() {
            nav.navigateAbsolute("registration/hidden")
        }

        override fun onDescriptionChange(text: String) {
            UserDescription = text
            description.value = text
        }

        override fun onBack() {
            nav.navigationBack()
        }

        override fun onNext() {
            nav.navigate("personal")
        }
    })
}