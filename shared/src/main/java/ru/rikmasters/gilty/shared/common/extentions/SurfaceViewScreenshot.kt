package ru.rikmasters.gilty.shared.common.extentions

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.PixelCopy
import com.pedro.rtplibrary.view.OpenGlView


fun OpenGlView.getBitmap(callback: (Bitmap?) -> Unit) {
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    try {
        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()
        PixelCopy.request(
            this, bitmap,
            { copyResult ->
                if (copyResult == PixelCopy.SUCCESS){
                    callback(bitmap)
                } else {
                    callback(null)
                }
                handlerThread.quitSafely()
            },
            Handler(handlerThread.looper)
        )
    } catch (e: IllegalArgumentException) {
        callback(null)
        e.printStackTrace()
    }
}

fun Bitmap.blur(context: Context?): Bitmap? {
    val width = Math.round(width * 0.1f)
    val height = Math.round(height * 0.1f)
    val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)
    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(15f)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
}