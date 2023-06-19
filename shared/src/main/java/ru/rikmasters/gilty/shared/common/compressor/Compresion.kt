@file:Suppress("DEPRECATION", "unused")

package ru.rikmasters.gilty.shared.common.compressor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapFactory.decodeFile
import android.graphics.Matrix
//noinspection ExifInterface
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.coroutines.CoroutineContext

object Compressor {
    
    suspend fun compress(
        context: Context,
        imageFile: File,
        coroutineContext: CoroutineContext = IO,
        compressionPatch: Compression.() -> Unit = { default() },
    ) = withContext(coroutineContext) {
        var result = copyToCache(context, imageFile)
        Compression()
            .apply(compressionPatch)
            .constraints
            .forEach { constraint ->
                while(constraint.isSatisfied(result).not())
                    result = constraint.satisfy(result)
            }
        return@withContext result
    }
}

private class DefaultConstraint(
    private val width: Int = 612,
    private val height: Int = 816,
    private val format: CompressFormat = JPEG,
    private val quality: Int = 80,
): Constraint {
    
    private var isResolved = false
    
    override fun isSatisfied(imageFile: File) = isResolved
    
    override fun satisfy(imageFile: File) =
        decodeSampledBitmapFromFile(
            imageFile = imageFile,
            reqWidth = width,
            reqHeight = height
        ).run {
            determineImageRotation(
                imageFile = imageFile,
                bitmap = this
            ).run {
                overWrite(
                    imageFile = imageFile,
                    bitmap = this,
                    format = format,
                    quality = quality
                )
            }
        }.let { isResolved = true; it }
}

fun Compression.default(
    width: Int = 612,
    height: Int = 816,
    format: CompressFormat = JPEG,
    quality: Int = 80,
) {
    constraint(
        DefaultConstraint(
            width = width,
            height = height,
            format = format,
            quality = quality
        )
    )
}

private val separator = File.separator

private fun cachePath(context: Context) =
    "${context.cacheDir.path}${separator}compressor${separator}"

fun File.compressFormat() = when(
    extension.lowercase(Locale.getDefault())) {
    "png" -> CompressFormat.PNG
    "webp" -> CompressFormat.WEBP
    else -> JPEG
}

fun CompressFormat.extension() = when(this) {
    CompressFormat.PNG -> "png"
    CompressFormat.WEBP -> "webp"
    else -> "jpg"
}

fun loadBitmap(imageFile: File) =
    decodeFile(imageFile.absolutePath).run {
        determineImageRotation(imageFile, this)
    }

fun decodeSampledBitmapFromFile(
    imageFile: File,
    reqWidth: Int,
    reqHeight: Int,
): Bitmap = Options().run {
    inJustDecodeBounds = true
    decodeFile(imageFile.absolutePath, this)
    
    inSampleSize = calculateInSampleSize(
        options = this,
        reqWidth = reqWidth,
        reqHeight = reqHeight
    )
    
    inJustDecodeBounds = false
    decodeFile(imageFile.absolutePath, this)
}

fun calculateInSampleSize(
    options: Options,
    reqWidth: Int,
    reqHeight: Int,
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1
    if(height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while(halfHeight / inSampleSize >=
            reqHeight && halfWidth / inSampleSize >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }
    
    return inSampleSize
}

fun determineImageRotation(imageFile: File, bitmap: Bitmap): Bitmap {
    val exif = ExifInterface(imageFile.absolutePath)
    val orientation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
    val matrix = Matrix()
    when(orientation) {
        6 -> matrix.postRotate(90f)
        3 -> matrix.postRotate(180f)
        8 -> matrix.postRotate(270f)
    }
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}

internal fun copyToCache(context: Context, imageFile: File): File {
    return imageFile.copyTo(
        File("${cachePath(context)}${imageFile.name}"),
        true
    )
}

fun overWrite(
    imageFile: File,
    bitmap: Bitmap,
    format: CompressFormat = imageFile.compressFormat(),
    quality: Int = 100,
): File {
    val result =
        if(format == imageFile.compressFormat()) imageFile else File(
            "${
                imageFile.absolutePath.substringBeforeLast(".")
            }.${format.extension()}"
        )
    imageFile.delete()
    saveBitmap(bitmap, result, format, quality)
    return result
}

fun saveBitmap(
    bitmap: Bitmap,
    destination: File,
    format: CompressFormat = destination.compressFormat(),
    quality: Int = 100,
) {
    destination.parentFile?.mkdirs()
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(destination.absolutePath)
        bitmap.compress(format, quality, fileOutputStream)
    } finally {
        fileOutputStream?.run {
            flush()
            close()
        }
    }
}

class Compression {
    
    val constraints: MutableList<Constraint> = mutableListOf()
    fun constraint(constraint: Constraint) {
        constraints.add(constraint)
    }
}

interface Constraint {
    
    fun isSatisfied(imageFile: File): Boolean
    
    fun satisfy(imageFile: File): File
}