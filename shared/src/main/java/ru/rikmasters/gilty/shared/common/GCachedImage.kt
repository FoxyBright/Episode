package ru.rikmasters.gilty.shared.common

import android.net.Uri.parse
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality.Companion.High
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter.State.Error
import coil.compose.AsyncImagePainter.State.Success
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy.DISABLED
import coil.request.CachePolicy.ENABLED
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import org.json.JSONObject
import ru.rikmasters.gilty.shared.common.CachedImageType.FILE
import ru.rikmasters.gilty.shared.common.CachedImageType.STORE
import ru.rikmasters.gilty.shared.common.CachedImageType.URL
import java.io.File
import kotlin.text.Charsets.UTF_8

enum class CachedImageType {
    URL, FILE, STORE
}

@Composable
fun GCachedImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = Fit,
    alignment: Alignment = Center,
    alpha: Float = DefaultAlpha,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null,
    placeholderColor: Color = Transparent,
    type: CachedImageType = URL,
    refreshImage: (() -> Unit)? = null,
) {
    
    val context = LocalContext.current
    val key = url?.getPhotoKey()
    
    val builder = key(url) {
        remember(url) {
            val builder = ImageRequest
                .Builder(context)
                .data(url)
                .size(ORIGINAL)
                .networkCachePolicy(ENABLED)
                .diskCachePolicy(DISABLED)
                .memoryCachePolicy(ENABLED)
                .allowHardware(false)
            key?.let { builder.memoryCacheKey(key) }
            builder.build()
        }
    }
    
    val painter = rememberAsyncImagePainter(
        builder, filterQuality = High,
        contentScale = contentScale
    )
    
    LaunchedEffect(painter.state) {
        if(painter.state is Error)
            refreshImage?.let { it() }
    }
    
    Image(
        painter = url?.let {
            when(type) {
                URL -> painter
                FILE -> rememberAsyncImagePainter(File(it))
                STORE -> painterResource(it.toInt())
            }
        } ?: rememberAsyncImagePainter(""),
        contentDescription = contentDescription,
        modifier = modifier.background(
            if(painter.state !is Success)
                placeholderColor
            else Transparent
        ), alignment = alignment,
        alpha = alpha,
        contentScale = contentScale,
        colorFilter = colorFilter
    )
}

private fun String.getPhotoKey() = try {
    parse(this)
        .getQueryParameter("policy")
        ?.let { policy ->
            Base64
                .decode(policy, DEFAULT)
                .toString(UTF_8)
                .let { JSONObject(it) }
                .let {
                    val path = it.getString("path")
                    val blur = it.getBoolean("blur")
                    "$path?blur=$blur"
                }
        }
} catch(e: Exception) {
    null
}