@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Unbounded
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun BlurBox(
    modifier: Modifier = Modifier,
    factor: Float = 1f,
    content: @Composable BoxScope.() -> Unit,
) {
    if(SDK_INT < 31)
        Box(modifier) {
            content()
            Box(
                modifier
                    .fillMaxSize()
                    .alpha(factor * 0.5f)
                    .background(colorScheme.background)
            )
        }
    else
        Box(
            modifier
                .fillMaxSize()
                .blur(
                    (factor * 10).dp,
                    Unbounded
                )
        ) { content() }
}

fun blur(context: Context, bitmap: Bitmap, radius: Int): Bitmap {
    val rs = RenderScript.create(context)
    val bitmapAlloc = Allocation.createFromBitmap(rs, bitmap)
    ScriptIntrinsicBlur.create(rs, bitmapAlloc.element).apply {
        setRadius(radius.toFloat())
        setInput(bitmapAlloc)
        forEach(bitmapAlloc)
    }
    bitmapAlloc.copyTo(bitmap)
    rs.destroy()
    return bitmap
}

@Composable
fun BackBlur(
    modifier: Modifier = Modifier,
    radius: Int = 10,
    view: View = LocalView.current,
    context: Context = LocalContext.current,
    content: @Composable BoxScope.() -> Unit,
) {
    view.isDrawingCacheEnabled = true
    val cache = view.drawingCache
    val image = remember(cache)
    { createBitmap(cache) }
    view.isDrawingCacheEnabled = false
    val blurImage = remember(image)
    { blur(context, image, radius).asImageBitmap() }
    Popup {
        Box(modifier) {
            if(SDK_INT < 31)
                Image(
                    blurImage, (null),
                    Modifier.fillMaxSize(),
                    contentScale = Crop
                )
            else Image(
                image.asImageBitmap(),
                (null), Modifier
                    .fillMaxSize()
                    .blur(radius.dp),
                contentScale = Crop
            )
            content()
        }
    }
}
