package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.GDropMenu
import ru.rikmasters.gilty.shared.shared.GKebabButton
import ru.rikmasters.gilty.shared.shared.METRICS
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun PhotoPreview() {
    GiltyTheme {
        PhotoView(
            PhotoViewState(
                DemoAvatarModel.id,
                (false), (0)
            )
        )
    }
}

@Preview
@Composable
private fun HiddenPhotoPreview() {
    GiltyTheme {
        PhotoView(
            PhotoViewState(
                DemoAvatarModel.id,
                (false), (1), (0.6f)
            )
        )
    }
}

data class PhotoViewState(
    val image: String,
    val menuState: Boolean,
    val type: Int,
    val load: Float? = null,
)

interface PhotoViewCallback {
    
    fun onMenuClick(state: Boolean) {}
    fun onMenuItemClick(point: Int) {}
    fun onBack() {}
}

@Composable
fun PhotoView(
    state: PhotoViewState,
    modifier: Modifier = Modifier,
    callback: PhotoViewCallback? = null
) {
    Column(modifier.background(colorScheme.background)) {
        when(state.type) {
            0 -> PhotoAppBar("1/1", Modifier, // TODO Тут должно показываться количество фоток
                { callback?.onBack() })
            { callback?.onMenuClick(true) }
            
            1 -> state.load?.let {
                HiddenPhotoAppBar(it)
                { callback?.onBack() }
            }
            
            else -> {}
        }
        AsyncImage(
            state.image, (null), Modifier
                .fillMaxSize()
                .weight(1f),
            contentScale = Fit
        )
    }
    GDropMenu(
        state.menuState, { callback?.onMenuClick(false) },
        DpOffset(
            ((METRICS.widthPixels / METRICS.density) - 160).dp, -(100).dp
        ), listOf(
            Pair(stringResource(R.string.edit_button))
            { callback?.onMenuItemClick(0) },
        )
    )
}

@Composable
private fun PhotoAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onMenuClick: (() -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(colorScheme.primaryContainer),
        SpaceBetween, CenterVertically
    ) {
        Row(Modifier, Start, CenterVertically) {
            Back(Modifier.padding(16.dp), onBack)
            Text(
                text, Modifier.padding(),
                colorScheme.tertiary,
                style = typography.headlineLarge
            )
        }
        onMenuClick?.let {
            GKebabButton(Modifier.padding(16.dp))
            { it() }
        }
    }
}

@Composable
private fun HiddenPhotoAppBar(
    load: Float,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(modifier) {
        Back(Modifier.padding(top = 24.dp), onBack)
        Loader(
            load, Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun Back(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick, modifier) {
        Icon(
            painterResource(ic_back),
            (null), Modifier.size(24.dp)
        )
    }
}

@Composable
private fun Loader(
    load: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                colorScheme.outline,
                CircleShape
            )
    ) {
        Box(
            Modifier
                .fillMaxWidth(load)
                .height(4.dp)
                .background(
                    linearGradient(red()),
                    CircleShape
                )
        )
    }
}