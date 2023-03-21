package ru.rikmasters.gilty.login.presentation.ui.gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ExifInterface.TAG_ORIENTATION
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.gallery.cropper.ImageCropper
import ru.rikmasters.gilty.login.viewmodel.GalleryViewModel
import java.io.File

@Composable
fun CropperScreen(vm: GalleryViewModel, image: String) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val file = File(image)
    var bitmap = BitmapFactory.decodeFile(
        file.absolutePath
    )
    
    try {
        val matrix = Matrix()
        matrix.postRotate(
            ExifInterface(file.absolutePath).getAttributeInt(
                TAG_ORIENTATION, 1
            ).toFloat()
        )
        bitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } catch(e: Exception) {
        e.printStackTrace()
    }
    
    Use<GalleryViewModel>(LoadingTrait) {
        ImageCropper(
            bitmap.asImageBitmap(), Modifier,
            { nav.navigationBack() }
        ) { _, list ->
            scope.launch {
                vm.setImage(file, list)
                nav.navigate("profile")
            }
        }
    }
}