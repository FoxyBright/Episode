package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import ru.rikmasters.gilty.shared.model.profile.EmojiModel

@Composable
fun GEmojiImage(
    emoji: EmojiModel,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        if(emoji.type == "D")
            painterResource(emoji.path.toInt())
        else rememberAsyncImagePainter(emoji.path),
        (null), modifier, alignment,
        contentScale, alpha, colorFilter
    )
}

