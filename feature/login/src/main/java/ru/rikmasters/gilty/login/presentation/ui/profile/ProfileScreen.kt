package ru.rikmasters.gilty.login.presentation.ui.profile

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.Avatar
import ru.rikmasters.gilty.login.viewmodel.Hidden
import ru.rikmasters.gilty.login.viewmodel.ProfileViewModel
import ru.rikmasters.gilty.shared.common.ProfileCallback
import ru.rikmasters.gilty.shared.common.ProfileState
import ru.rikmasters.gilty.shared.model.profile.EmojiList
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    photo: String = "",
    hiddenPhoto: String = ""
) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val occupied by vm.occupied.collectAsState()
    val username by vm.username.collectAsState()
    val description by vm.description.collectAsState()
    
    val profileState = ProfileState(
        name = username,
        hiddenPhoto = if(hiddenPhoto == "") Hidden else hiddenPhoto,
        profilePhoto = if(photo == "") Avatar else photo,
        description = description,
        rating = "0.0",
        emoji = EmojiList.first(),
        enabled = true,
        occupiedName = occupied
    )
    
    val imageData = remember {
        mutableStateOf<Uri?>(null)
    }
    
    val launcher =
        rememberLauncherForActivityResult(GetContent()) {
            imageData.value = it
        }
    
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        scope.launch {
            vm.setAvatar(getImageFile(imageData.value, context))
        }
    }
    
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
                launcher.launch("image/*") // TODO Сделать нормальный доступ к фото
                //nav.navigateAbsolute("registration/gallery?multi=false")
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

@Suppress("deprecation")
private fun getImageFile(imageData: Uri?, context: Context): File {
    
    var imageBitmap: Bitmap? = null
    
    val file = File(
        ContextWrapper(context).getDir(
            "Images", MODE_PRIVATE
        ), ("${UUID.randomUUID()}.jpg")
    )
    
    val stream: OutputStream = FileOutputStream(file)
    
    imageData?.let {
        imageBitmap = if(Build.VERSION.SDK_INT < 28) {
            MediaStore.Images
                .Media.getBitmap(context.contentResolver, it)
        } else {
            ImageDecoder.decodeBitmap(
                ImageDecoder
                    .createSource(context.contentResolver, it)
            )
        }
    }
    
    imageBitmap?.compress(JPEG, 25, stream)
    stream.flush()
    stream.close()
    return file
}