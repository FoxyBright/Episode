package ru.rikmasters.gilty.login.presentation.ui.profile

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.*
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.gallery.checkStoragePermission
import ru.rikmasters.gilty.gallery.permissionState
import ru.rikmasters.gilty.login.viewmodel.ProfileViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.profile.ProfileModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(vm: ProfileViewModel) {
    
    val storagePermissions = permissionState()
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val description by vm.description.collectAsState()
    val profile by vm.profile.collectAsState()
    val occupied by vm.occupied.collectAsState()
    val username by vm.username.collectAsState()
    
    val regexError = username.contains(Regex("[^A-Za-z]"))
    val shortUserNameError = username.length in 1 until 4
    var errorAvatar by remember { mutableStateOf(false) }
    val longUserNameError = username.length > 20
    
    val regexString =
        stringResource(R.string.profile_username_error_regex)
    val longUsernameString =
        stringResource(R.string.profile_long_username)
    val shortUsernameString =
        stringResource(R.string.profile_short_username)
    val usernameOccupiedString =
        stringResource(R.string.profile_user_name_is_occupied)
    
    var errorText by remember(username, occupied) {
        mutableStateOf(
            when {
                regexError -> regexString
                longUserNameError -> longUsernameString
                shortUserNameError -> shortUsernameString
                occupied -> usernameOccupiedString
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
            rating = RatingModel(
                average = "4.9",
                emoji = getEmoji("HEART_EYES")
            )
        ), errorText = errorText,
        isError = errorAvatar
    )
    
    val isActive = username.isNotBlank()
            && !occupied
            && !errorAvatar
            && errorText.isBlank()
            && !profile?.avatar
        ?.thumbnail?.url.isNullOrBlank()
    
    ProfileContent(
        profileState, isActive, Modifier,
        object: ProfileCallback {
            
            override fun onDisabledButtonClick() {
                errorAvatar = profile?.avatar?.thumbnail?.url == null
                if(errorAvatar || username.isBlank())
                    errorText = "Обязательное поле"
            }
            
            @SuppressLint("SuspiciousIndentation")
            override fun profileImage() {
                context.checkStoragePermission(
                    storagePermissions, scope, asm,
                ) {
                    nav.navigateAbsolute(
                        "registration/gallery?multi=false"
                    )
                }
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
                scope.launch {
                    vm.checkOnNext()
                    nav.navigate("personal")
                }
            }
        }
    )
}