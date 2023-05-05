package ru.rikmasters.gilty.shared.common

import android.net.Uri.parse
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality.Companion.High
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter.State.Success
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy.ENABLED
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import org.json.JSONObject
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors
import kotlin.text.Charsets.UTF_8

@Composable
fun GCashedImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null,
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
                .diskCachePolicy(ENABLED)
                .memoryCachePolicy(ENABLED)
                .allowHardware(false)
            key?.let {
                builder.memoryCacheKey(it)
                builder.diskCacheKey(it)
            }
            builder.build()
        }
    }
    
    val painter = rememberAsyncImagePainter(
        builder, filterQuality = High,
        contentScale = contentScale
    )
    
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.background(
            if(painter.state !is Success)
                colors.meetCardPlaceHolder
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