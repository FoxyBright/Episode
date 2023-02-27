package ru.rikmasters.gilty.chat.presentation.ui.chat.bottom

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.gallery.GalleryCallback
import ru.rikmasters.gilty.gallery.GalleryGrid
import ru.rikmasters.gilty.gallery.GalleryState
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.EmptyScreen
import ru.rikmasters.gilty.shared.shared.GradientButton

@Composable
fun HiddenBsContent(
    state: GalleryState,
    modifier: Modifier,
    callback: GalleryCallback? = null,
) {
    if(state.images.isEmpty())
        EmptyHiddenBs()
    else Column(
        modifier
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
    ) {
        Text(
            stringResource(R.string.profile_hidden_photo),
            Modifier.padding(bottom = 14.dp),
            style = typography.labelLarge,
        )
        GalleryGrid(
            Modifier.weight(1f), state.images,
            state.selectedImages,
            state.type, (true), callback
        )
        GradientButton(
            Modifier.padding(vertical = 28.dp),
            state.buttonLabel,
            state.buttonEnabled,
            state.isOnline
        ) { callback?.onAttach() }
    }
}

@Composable
private fun EmptyHiddenBs(
    modifier: Modifier = Modifier,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            stringResource(R.string.profile_hidden_photo),
            modifier.align(TopStart),
            colorScheme.tertiary,
            style = typography.labelLarge
        )
        EmptyScreen(
            stringResource(R.string.chats_empty_hidden_photos),
            R.drawable.ic_image_box, Modifier, 50.dp, 4.dp,
        )
    }
}