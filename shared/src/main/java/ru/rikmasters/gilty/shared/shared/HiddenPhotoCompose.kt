package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.model.profile.ImageModel
import ru.rikmasters.gilty.shared.theme.Gradients
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun HiddenImageLockPreview() {
    GiltyTheme { HiddenImage(DemoAvatarModel, Modifier.padding(4.dp)) }
}

@Preview
@Composable
private fun HiddenImageUnlockPreview() {
    GiltyTheme { HiddenImage(DemoAvatarModel, Modifier.padding(4.dp), (false)) }
}

@Composable
fun HiddenImage(
    image: ImageModel,
    modifier: Modifier = Modifier,
    hidden: Boolean = true,
    onClick: ((ImageModel) -> Unit)? = null
) {
    Box(
        modifier
            .size(50.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable { onClick?.let { it(image) } }
    ) {
        if (hidden) Box(
            Modifier
                .background(Brush.linearGradient(Gradients.red()))
                .fillMaxSize()
        ) else AsyncImage(
            image.id, (null), Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.gb)
        )
        CheckBox(
            !hidden, Modifier.size(24.dp), listOf(
                R.drawable.ic_lock_open,
                R.drawable.ic_lock_close
            ), Color.White
        ) { onClick?.let { it(image) } }
    }
}