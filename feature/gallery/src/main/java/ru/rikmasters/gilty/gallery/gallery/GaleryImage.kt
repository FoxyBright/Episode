package ru.rikmasters.gilty.gallery.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.shared.common.CachedImageType.FILE
import ru.rikmasters.gilty.shared.common.CachedImageType.WEB
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.shared.CheckBox

enum class GalleryImageType { MULTIPLE, SINGLE }

@Composable
fun GalleryImage(
    image: String,
    type: GalleryImageType,
    selected: Boolean?,
    modifier: Modifier = Modifier,
    isWeb: Boolean = false,
    callback: GalleryCallback? = null,
) {
    Box(
        modifier
            .size(115.dp)
            .clickable {
                callback?.onImageClick(image)
            }
    ) {
        GCachedImage(
            image, Modifier.fillMaxSize(),
            contentScale = Crop,
            type = if(isWeb) WEB else FILE
        )
        if(type == MULTIPLE) {
            CheckBox(
                (selected ?: false),
                Modifier
                    .align(TopEnd)
                    .padding(8.dp)
            ) { callback?.onImageClick(image) }
        }
    }
}