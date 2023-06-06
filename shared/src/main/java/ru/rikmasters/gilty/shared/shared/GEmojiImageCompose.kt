package ru.rikmasters.gilty.shared.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import ru.rikmasters.gilty.shared.common.CachedImageType.RESOURCE
import ru.rikmasters.gilty.shared.common.CachedImageType.WEB
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.model.image.EmojiModel

@Composable
fun GEmojiImage(
    emoji: EmojiModel?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {
    emoji?.let {
        GCachedImage(
            url = it.path,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            type = if(it.type != "URL")
                RESOURCE else WEB
        )
    }
}

