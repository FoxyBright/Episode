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
    
    var errorAvatar by remember { mutableStateOf(false) }
    val longUserNameError = username.length > 20
    val shortUserNameError = username.length in 1 until 4
    val regexError = username.contains(
        Regex("[^A-Za-z\\d]")
    )
    
    
    var errorText by remember(username) {
        mutableStateOf(
            when {
                regexError -> "Имя пользователя содержит недопустимые символы"
                longUserNameError -> "Превышен лимит в 20 символов"
                shortUserNameError -> "Введите минимум 4 символа"
                occupied -> "Имя пользователя занято"
                else -> ""
            }
        )
    }
    
    LaunchedEffect(Unit) {
        vm.getProfile()
        errorText = ""
    }
    
    val profileState = ProfileState(
        ProfileModel().copy(
            username = username,
            hidden = profile?.hidden,
            avatar = profile?.avatar,
            aboutMe = description,
        ), errorText = errorText,
        isError = errorAvatar
    )
    
    val isActive = username.isNotBlank()
            && !occupied && !profile?.avatar
        ?.thumbnail?.url.isNullOrBlank()
    
    ProfileContent(
        profileState, isActive, Modifier,
        object: ProfileCallback {
            override fun onDisabledButtonClick() {
                errorAvatar = profile?.avatar?.thumbnail?.url == null
                errorText = if(errorAvatar || username.isBlank())
                    "Обязательное поле" else ""
            }
            
            override fun profileImage() {
                nav.navigateAbsolute(
                    "registration/gallery?multi=false"
                )
            }
            
            override fun hiddenImages() {
                nav.navigate("hidden")
            }
            
            override fun onDescriptionChange(text: String) {
                scope.launch {
                    vm.descriptionChange(text)
                    errorAvatar = false
                }
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
                scope.launch {
                    vm.clearLoginData()
                    nav.navigateAbsolute("login")
                }
            }
            
            override fun onNext() {
                nav.navigate("personal")
            }
        }
    )
}