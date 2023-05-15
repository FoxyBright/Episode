package ru.rikmasters.gilty.translation.presentation.ui.content

import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.rtplibrary.view.AspectRatioMode
import com.pedro.rtplibrary.view.OpenGlView

// DONE REFACTOR
@Composable
fun Camera(
    startPreview:() -> Unit,
    stopPreview:() -> Unit,
    initCamera: (OpenGlView) -> Unit,
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val view = OpenGlView(it)
            view.keepScreenOn = true
            view.isKeepAspectRatio = true
            view.setAspectRatioMode(AspectRatioMode.Fill)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val surfaceHolderCallback = object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {}
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int,
                ) {
                    startPreview()
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    stopPreview()
                }
            }
            view.holder.addCallback(surfaceHolderCallback)
            initCamera(view)
            Log.d("TEST","INITED")
            view
        },
        update = {}
    )
}