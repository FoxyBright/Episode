package ru.rikmasters.gilty.shared.common.blur

import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RealTimeBlur(
    modifier: Modifier = Modifier,
    size: IntOffset = IntOffset(300,100),
    blurRadius: Int = 40,
    shape: Shape = RoundedCornerShape(38.dp),
    backgroundColor: Color = Color.White,
    backgroundColorAlpha: Float = 0.4f,
    dialogDimAmount: Float? = null,
    dialogBehindBlurRadius: Int = 0,
    isRealtime: Boolean = true,
    content: @Composable () -> Unit,
){
    val captureController = rememberCaptureController()

    var bitmap by remember { mutableStateOf(ImageBitmap(size.x, size.y)) }
    var dialogPos by remember { mutableStateOf(Offset.Zero) }
    val paint = Paint().apply {
        this.shader = shader
    }

    Capturable(
        controller = captureController,
        onCaptured = { b, _ ->
            b?.let {
                if(it.asAndroidBitmap().sameAs(bitmap.asAndroidBitmap()).not()){
                    bitmap= it
                    val path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM)
                    File(path, "screenshotppp.png")
                        .writeBitmap(bitmap.asAndroidBitmap(), Bitmap.CompressFormat.PNG, 30)

                }
            }
        },
        ) {
        Box(modifier = modifier
            .border(width = 1.dp, color = Color.Red)
            .clip(shape)
        ) {
            content()
        }
    }

    LaunchedEffect(Unit) {
        do{
            captureController.capture()
            delay(30)
        }while (isRealtime)
    }
}