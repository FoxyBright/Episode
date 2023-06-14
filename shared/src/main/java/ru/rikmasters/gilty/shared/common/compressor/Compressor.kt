@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.shared.common.compressor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.Bitmap.createScaledBitmap
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.core.net.toUri
import ru.rikmasters.gilty.data.ktor.Ktor.logD
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

suspend infix fun File.compress(
    context: Context,
) = getBitmap(context.contentResolver, this.toUri())
    ?.let { resizeBitmap(it) }
    ?.let { bit ->
        val file = File(
            context.filesDir,
            "$nameWithoutExtension.jpg"
        )
        BufferedOutputStream(
            FileOutputStream(file)
        ).use { bit.compress(JPEG, 80, it) }
        file
    }?.let { Compressor.compress(context, it) }!!.also {
        logD(it.absolutePath.toString()) }

private fun resizeBitmap(
    image: Bitmap,
    maxHeight: Int = 1200,
    maxWidth: Int = 1200,
) = if(maxHeight > 0 && maxWidth > 0)
    ((image.width.toFloat() / image.height.toFloat()) to
            (maxWidth.toFloat() / maxHeight.toFloat()))
        .let { (sourceRatio, source) ->
            createScaledBitmap(
                image, if(source > sourceRatio)
                    (maxHeight.toFloat() * sourceRatio).toInt()
                else maxWidth, if(source <= sourceRatio)
                    (maxWidth.toFloat() / sourceRatio).toInt()
                else maxHeight, (true)
            )!!
        }
else image