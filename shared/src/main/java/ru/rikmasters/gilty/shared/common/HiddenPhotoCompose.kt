package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.ActionBar
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra.colors

@Preview
@Composable
private fun HiddenPhotoPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            HiddenContent(
                HiddenState(listOf(),0)
            )
        }
    }
}

data class HiddenState(
    val photoList: List<String>,
    val photosAmount:Int,
)

interface HiddenCallback {
    
    fun onNext() {}
    fun onBack() {}
    fun onSelectImage(image: String) {}
    fun onDeleteImage(image: String) {}
    fun openGallery() {}
}

@Composable
fun HiddenContent(
    state: HiddenState,
    modifier: Modifier = Modifier,
    callback: HiddenCallback? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
    ) {
        ActionBar(
            stringResource(R.string.profile_hidden_photo),
            Modifier.padding(bottom = 20.dp),
            stringResource(R.string.profile_hidden_photo_label),
            extra = stringResource(R.string.profile_hidden_photo_amount, state.photosAmount)
        ) { callback?.onBack() }
        LazyVerticalGrid(
            GridCells.Fixed(3),
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = spacedBy(4.dp),
            horizontalArrangement = spacedBy(4.dp)
        ) {
            item { GalleryButton(callback) }
            items(state.photoList) { image ->
                LazyItem(
                    image, Modifier,
                    { callback?.onSelectImage(it) })
                { callback?.onDeleteImage(it) }
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        GradientButton(
            Modifier
                .padding(bottom = 48.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter),
            stringResource(R.string.next_button)
        ) { callback?.onNext() }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GalleryButton(callback: HiddenCallback?) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LazyItem(
    image: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    Box(
        modifier
            .size(130.dp)
            .clip(shapes.small)
            .clickable { onSelect(image) },
        TopEnd
    ) {
        GCachedImage(
            image, Modifier.fillMaxSize(),
            contentScale = Crop,
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
                    Filled.Close, (null),
                    Modifier.size(16.dp), White
                )
            }
        }
    }
}