package ru.rikmasters.gilty.shared.common

import android.net.Uri
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
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
import coil.memory.MemoryCache
import coil.request.CachePolicy.ENABLED
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import org.json.JSONObject
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.util.extension.slash
import ru.rikmasters.gilty.data.ktor.Ktor.logE
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
    
    val env = get<Environment>()
    
    val baseUrl: String = env["WEB_BASE_URL"]
        ?: "gilty.rikmasters.ru/api/v1".also {
            logE("Не задана переменная среды WEB_BASE_URL")
        }
    
    val host = remember(baseUrl) {
        Uri.parse(baseUrl).let {
            val scheme = "http://"
            scheme.plus(Uri.parse(scheme.plus(it)).host)
        }.log()
    }
    
    val builder = remember(url) {
        val pair = getPhotoKey(host, url)
        val builder = ImageRequest.Builder(context)
            .data(pair?.first)
            .size(ORIGINAL)
            .allowHardware(false)
        pair?.let {
            builder
                .memoryCacheKey(pair.memoryCacheKey())
                .diskCacheKey(pair.diskCacheKey())
        } ?: run {
            builder.diskCacheKey(url)
                .memoryCacheKey(url)
                .networkCachePolicy(ENABLED)
                .diskCachePolicy(ENABLED)
                .memoryCachePolicy(ENABLED)
        }
        
        builder.build()
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

private fun Pair<String, *>?.memoryCacheKey() = this?.run {
    MemoryCache.Key(first, mapOf("extraKey" to second.toString()))
}

private fun Pair<String, *>?.diskCacheKey() = this?.run {
    "$first&extraKey=${second}"
}

private fun getPhotoKey(
    host: String,
    url: String?,
): Pair<String, Boolean>? {
    return url?.let {
        try {
            val uri = Uri.parse(url)
            
            val hash = uri.getQueryParameter("hash")
            
            val policy = uri.getQueryParameter("policy")
                ?: throw IllegalStateException("Нет поля policy в $url")
            
            val obj = Base64.decode(policy, DEFAULT).toString(UTF_8)
            
            val json = JSONObject(obj)
            
            val path = json.getString("path")
            val blur = json.getBoolean("blur")
            
            (host slash path).plus("?policy=$policy&hash=$hash") to blur
        } catch(e: Exception) {
            e.stackTraceToString()
            logE("Проблемы кэширования фото URL: $url")
            null
        }
    } ?: run {
        logE("Проблемы кэширования фото URL: $url")
        null
    }
}