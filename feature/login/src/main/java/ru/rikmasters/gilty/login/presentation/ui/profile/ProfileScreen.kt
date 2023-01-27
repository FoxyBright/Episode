package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.Avatar
import ru.rikmasters.gilty.login.viewmodel.Hidden
import ru.rikmasters.gilty.login.viewmodel.ProfileViewModel
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.image.DemoThumbnailModel
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    photo: String = "",
    hiddenPhoto: String = "",
) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val occupied by vm.occupied.collectAsState()
    val username by vm.username.collectAsState()
    val description by vm.description.collectAsState()
    
    val profileState = ProfileState(
        ProfileModel.empty.copy(
            username = username,
            hidden = DemoThumbnailModel.copy(
                id = if(hiddenPhoto == "") Hidden
                else hiddenPhoto
            ).map(),
            avatar = DemoThumbnailModel.copy(
                id = if(photo == "")
                    Avatar else photo
            ).map(),
            aboutMe = description,
        ),
        occupiedName = occupied
    )
    
    ProfileContent(
        profileState, Modifier,
        object: ProfileCallback {
            override fun onNameChange(text: String) {
                scope.launch { vm.usernameChange(text) }
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch { vm.descriptionChange(text) }
            }
            
            override fun profileImage() {
                nav.navigateAbsolute("registration/gallery?multi=false")
            }
            
            override fun hiddenImages() {
                nav.navigateAbsolute("registration/hidden")
            }
            
            override fun onBack() {
                nav.navigateAbsolute("login")
            }
            
            override fun onNext() {
                nav.navigate("personal")
            }
        })
}