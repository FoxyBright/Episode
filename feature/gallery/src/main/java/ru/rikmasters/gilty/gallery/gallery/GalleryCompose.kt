package ru.rikmasters.gilty.gallery.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.MULTIPLE
import ru.rikmasters.gilty.gallery.gallery.GalleryImageType.SINGLE
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun GallerySinglePreview() {
    GiltyTheme {
        GalleryContent(
            GalleryState(SINGLE)
        )
    }
}

@Preview
@Composable
private fun GalleryMultiplePreview() {
    GiltyTheme {
        GalleryContent(
            GalleryState(
                MULTIPLE, buttonLabel =
                stringResource(R.string.send_button)
            )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GalleryContent(
    state: GalleryState,
    modifier: Modifier = Modifier,
    callback: GalleryCallback? = null,
) {
    Scaffold(
        modifier.systemBarsPadding()
            .navigationBarsPadding(), {
        GalleryDropMenu(
            state = state.menuState,
            menuList = state.menuItems,
            callback = callback
        )
        GalleryTopBar(
            menuState = state.menuState,
            callback = callback
        )
    }) { paddings ->
        GalleryGrid(
            modifier = modifier.padding(
                top = paddings.calculateTopPadding()
            ),
            images = state.images,
            selected = state.selectedImages,
            type = state.type,
            isWeb = (false),
            callback = callback
        )
    }
    if(state.type == MULTIPLE)
        Box(Modifier.fillMaxSize()) {
            GradientButton(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .padding(horizontal = 16.dp)
                    .align(BottomCenter),
                text = state.buttonLabel
            ) { callback?.onAttach() }
        }
}