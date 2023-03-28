package ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

interface HiddenBsCallback {
    
    fun onSelectImage(image: AvatarModel) {}
    fun onDeleteImage(image: AvatarModel) {}
    fun openGallery() {}
}

@Composable
fun HiddenBsContent(
    photoList: List<AvatarModel>,
    modifier: Modifier = Modifier,
    callback: HiddenBsCallback? = null
) {
    Column(modifier) {
        Text(
            stringResource(R.string.profile_hidden_photo),
            Modifier
                .padding(16.dp)
                .padding(top = 12.dp),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        LazyVerticalGrid(
            Fixed(3), Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = spacedBy(4.dp),
            horizontalArrangement = spacedBy(4.dp)
        ) {
            item { GalleryButton(callback) }
            items(photoList) { image ->
                LazyItem(
                    image.thumbnail.url, Modifier,
                    { callback?.onSelectImage(image) })
                { callback?.onDeleteImage(image) }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GalleryButton(
    callback: HiddenBsCallback?
) {
    Card(
        { callback?.openGallery() },
        Modifier.size(130.dp),
        shape = shapes.large,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.grayButton),
            Center
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(colorScheme.primary)
            ) {
                Icon(
                    painterResource(R.drawable.ic_image_box),
                    (null), Modifier
                        .padding(6.dp)
                        .size(32.dp), White
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LazyItem(
    image: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Box(
        modifier
            .size(130.dp)
            .clip(shapes.small)
            .clickable { onSelect(image) },
        TopEnd
    ) {
        AsyncImage(
            image, (null),
            Modifier.fillMaxSize(),
            contentScale = Crop
        )
        Card(
            { onDelete(image) },
            Modifier
                .padding(4.dp)
                .size(26.dp)
                .clip(CircleShape)
                .align(TopEnd)
                .alpha(50f),
            colors = cardColors(Transparent)
        ) {
            Box(Modifier, Center) {
                Image(
                    painterResource(R.drawable.transparency_circle),
                    (null), Modifier.fillMaxSize()
                )
                Icon(
                    Icons.Filled.Close, (null),
                    Modifier.size(16.dp), White
                )
            }
        }
    }
}