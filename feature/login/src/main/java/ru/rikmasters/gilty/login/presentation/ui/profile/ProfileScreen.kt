package ru.rikmasters.gilty.login.presentation.ui.profile

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.ProfileViewModel
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.profile.ProfileModel

@Composable
fun ProfileScreen(vm: ProfileViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val description by vm.description.collectAsState()
    val profile by vm.profile.collectAsState()
    val occupied by vm.occupied.collectAsState()
    val username by vm.username.collectAsState()
    
    LaunchedEffect(Unit) { vm.getProfile() }
    
    val profileState = ProfileState(
        ProfileModel.empty.copy(
            username = username,
            hidden = profile?.hidden,
            avatar = profile?.avatar,
            aboutMe = description,
        ),
        occupiedName = occupied
    )
    
    val isActive = username.isNotBlank()
            && !occupied && !profile?.avatar
        ?.thumbnail?.url.isNullOrBlank()
    
    ProfileContent(
        profileState, isActive, Modifier,
        object: ProfileCallback {
            
            override fun profileImage() {
                nav.navigateAbsolute("registration/gallery?multi=false")
            }
            
            override fun hiddenImages() {
                nav.navigateAbsolute("registration/hidden")
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch { vm.descriptionChange(text) }
            }
            
            override fun onSaveDescription() {
                scope.launch { vm.onDescriptionSave() }
            }
            
            override fun onNameChange(text: String) {
                scope.launch { vm.usernameChange(text) }
            }
            
            override fun onSaveUserName() {
                scope.launch { vm.onUsernameSave() }
            }
            
            override fun onBack() {
                nav.navigateAbsolute("login")
            }
            
            override fun onNext() {
                nav.navigate("personal")
            }
        }
    )
}