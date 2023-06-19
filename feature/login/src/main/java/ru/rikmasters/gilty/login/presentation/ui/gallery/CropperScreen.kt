package ru.rikmasters.gilty.login.presentation.ui.gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ExifInterface.TAG_ORIENTATION
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.cropper.GImageCropperCallback
import ru.rikmasters.gilty.gallery.cropper.GImageCropperState
import ru.rikmasters.gilty.gallery.cropper.ImageCropper
import ru.rikmasters.gilty.login.viewmodel.GalleryViewModel
import java.io.File

@Composable
fun CropperScreen(vm: GalleryViewModel, image: String) {
    
    val scope = rememberCoroutineScope()
    
    val nav = get<NavState>()
    val file = File(image)
    var bitmap = BitmapFactory
        .decodeFile(
            
            file.absolutePath
        )
    val orientation = ExifInterface(file.absolutePath)
        .getAttributeInt(TAG_ORIENTATION, (1))
    
    try {
        bitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width,
            bitmap.height,
            Matrix().also {
                it.postRotate(
                    when(orientation) {
                        6 -> 90f
                        3 -> 180f
                        8 -> 270f
                        else -> 0f
                    }
                )
            }, true
        )
    } catch(e: Exception) {
        e.printStackTrace()
    }
    
    Use<GalleryViewModel>(LoadingTrait) {
        ImageCropper(
            state = GImageCropperState(
                image = bitmap.asImageBitmap()
            ),
            callback = object: GImageCropperCallback {
                
                override fun onCrop(
                    img: ImageBitmap,
                    list: List<Float>,
                ) {
                    scope.launch {
                        vm.setImage(file, list)
                        nav.navigate("profile")
                    }
                }
                
                override fun onBack() {
                    nav.navigationBack()
                }
            }
        )
    }
}