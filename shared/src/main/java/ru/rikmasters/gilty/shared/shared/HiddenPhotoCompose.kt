package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_lock_close
import ru.rikmasters.gilty.shared.R.drawable.ic_lock_open
import ru.rikmasters.gilty.shared.common.GCachedImage
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun HiddenImageLockPreview() {
    GiltyTheme {
        HiddenImage(
            DemoAvatarModel,
            Modifier.padding(4.dp)
        )
    }
}

@Preview
@Composable
private fun HiddenImageUnlockPreview() {
    GiltyTheme {
        HiddenImage(
            DemoAvatarModel,
            Modifier.padding(4.dp),
            (false)
        )
    }
}

@Composable
fun HiddenImage(
    image: AvatarModel?,
    modifier: Modifier = Modifier,
    hidden: Boolean = true,
    onClick: ((AvatarModel?) -> Unit)? = null,
) {
    Box(
        modifier
            .size(50.dp)
            .clip(shapes.extraSmall)
            .clickable(
                MutableInteractionSource(), (null)
            ) { onClick?.let { it(image) } }
    ) {
        if(!hidden) Box(
            Modifier
                .background(linearGradient(red()))
                .fillMaxSize()
        ) else GCachedImage(
            image?.thumbnail?.url, Modifier
                .background(colorScheme.onTertiary)
                .fillMaxSize(),
            contentScale = Crop,
        )
        CheckBox(
            hidden, Modifier
                .size(24.dp)
                .align(Center),
            listOf(ic_lock_open, ic_lock_close), White
        ) { onClick?.let { it(image) } }
    }
}