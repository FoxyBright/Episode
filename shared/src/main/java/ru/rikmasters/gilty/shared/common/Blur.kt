@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun BlurBox(
    modifier: Modifier = Modifier,
    factor: Float = 1f,
    content: @Composable BoxScope.() -> Unit
) {
    if(Build.VERSION.SDK_INT < 31) Box(modifier){
        content()
        Box(
            Modifier
                .fillMaxSize()
                .alpha(factor * 0.5f)
                .background(colorScheme.background)
        )
    } else Box(
        modifier
            .fillMaxSize()
            .blur((factor * 10).dp, BlurredEdgeTreatment.Unbounded)
    ) {
        content()
    }
}

private fun View.bitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun blur(context: Context, bitmap: Bitmap): Bitmap {
    val rs = RenderScript.create(context)
    val bitmapAlloc = Allocation.createFromBitmap(rs, bitmap)
    ScriptIntrinsicBlur.create(rs, bitmapAlloc.element).apply {
        setRadius(10f);setInput(bitmapAlloc); forEach(bitmapAlloc)
    }; bitmapAlloc.copyTo(bitmap); rs.destroy(); return bitmap
}

@Composable
fun getView() = LocalView.current
@Composable
fun getContext() = LocalContext.current

@Composable
fun BackBlur(
    modifier: Modifier = Modifier,
    view: View = getView(),
    context: Context= getContext(),
    content: @Composable BoxScope.() -> Unit
) {
    view.isDrawingCacheEnabled = true
    val cache = view.drawingCache
    val image = remember(cache)
    { Bitmap.createBitmap(cache) }
    view.isDrawingCacheEnabled = false
    val blurImage = remember(image)
    { blur(context, image).asImageBitmap() }
    Popup {
        Box(modifier) {
            if (Build.VERSION.SDK_INT < 31)
                Image(
                    blurImage, null,
                    Modifier.fillMaxSize()
                )
            else Image(
                image.asImageBitmap(),
                (null), Modifier
                    .fillMaxSize()
                    .blur(10.dp)
            )
            content()
        }
    }
}
