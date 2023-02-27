package ru.rikmasters.gilty.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.gallery.GalleryImageType.SINGLE
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun GalleryBsPreview() {
    GiltyTheme {
        Box(
            Modifier.background(
                colorScheme.background
            )
        ) {
            GalleryBsContent(
                GalleryState(
                    SINGLE, buttonLabel =
                    stringResource(R.string.send_button)
                )
            )
        }
    }
}

@Composable
fun GalleryBsContent(
    state: GalleryState,
    modifier: Modifier = Modifier,
    callback: GalleryCallback? = null,
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
    ) {
        Text(
            stringResource(R.string.profile_gallery_title),
            Modifier.padding(bottom = 14.dp),
            style = typography.labelLarge,
        )
        GalleryGrid(
            Modifier.weight(1f), state.images,
            state.selectedImages,
            state.type, (false), callback
        )
        GradientButton(
            Modifier.padding(vertical = 28.dp),
            state.buttonLabel,
            state.buttonEnabled,
            state.isOnline
        ) { callback?.onAttach() }
    }
}