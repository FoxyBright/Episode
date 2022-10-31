@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.common

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

fun blur(context: Context, bitmap: Bitmap): Bitmap {
    val rs = RenderScript.create(context)
    val bitmapAlloc = Allocation.createFromBitmap(rs, bitmap)
    ScriptIntrinsicBlur.create(rs, bitmapAlloc.element).apply {
        setRadius(10f);setInput(bitmapAlloc); forEach(bitmapAlloc)
    }; bitmapAlloc.copyTo(bitmap); rs.destroy(); return bitmap
}

@Composable
fun BackBlur(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    LocalView.current.isDrawingCacheEnabled = true
    val cache = LocalView.current.drawingCache
    val image = remember(cache)
    { Bitmap.createBitmap(cache) }
    LocalView.current.isDrawingCacheEnabled = false
    val context = LocalContext.current
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
                null,
                Modifier
                    .fillMaxSize()
                    .blur(10.dp)
            )
            content()
        }
    }
}
