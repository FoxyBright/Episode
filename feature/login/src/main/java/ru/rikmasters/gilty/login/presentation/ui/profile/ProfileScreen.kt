package ru.rikmasters.gilty.login.presentation.ui.profile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.ProfileViewModel
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel

@OptIn(ExperimentalPermissionsApi::class)
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
    
    val permission = rememberPermissionState(
        if(SDK_INT < TIRAMISU)
            READ_EXTERNAL_STORAGE
        else READ_MEDIA_IMAGES
    )
    
    val profileState = ProfileState(
        ProfileModel().copy(
            username = username,
            hidden = profile?.hidden,
            avatar = profile?.avatar,
            aboutMe = description,
            rating = RatingModel(
                average = "4.9",
                emoji = getEmoji("HEART_EYES")
            )
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
            
            @SuppressLint("SuspiciousIndentation")
            override fun profileImage() {
                if(permission.hasPermission)
                    nav.navigateAbsolute(
                        "registration/gallery?multi=false"
                    )
                else scope.launch {
                    if(permission.shouldShowRationale)
                        permission.launchPermissionRequest()
                }
            }
            
            override fun hiddenImages() {
                if(permission.hasPermission)
                    nav.navigate("hidden")
                else scope.launch {
                    permission.launchPermissionRequest()
                }
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