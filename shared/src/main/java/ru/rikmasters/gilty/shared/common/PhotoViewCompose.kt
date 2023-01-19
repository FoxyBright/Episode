package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.*
import androidx.compose.foundation.MutatePriority.PreventUserInput
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.data.ktor.Ktor.logD
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.GDropMenu
import ru.rikmasters.gilty.shared.shared.GKebabButton
import ru.rikmasters.gilty.shared.shared.METRICS
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import kotlin.math.*

@Preview
@Composable
private fun PhotoPreview() {
    GiltyTheme {
        PhotoView(
            PhotoViewState(
                DemoAvatarModel.id,
                ("1/1"), (false), (0)
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
                DemoAvatarModel.id, ("1/1"),
                (false), (1), (0.6f)
            )
        )
    }
}

data class PhotoViewState(
    val image: String,
    val imageCount: String,
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
@OptIn(ExperimentalMaterial3Api::class)
fun PhotoView(
    state: PhotoViewState,
    modifier: Modifier = Modifier,
    callback: PhotoViewCallback? = null
) {
    Scaffold(
        modifier.background(
            colorScheme.background
        ), {
            when(state.type) {
                0 -> PhotoAppBar(
                    state.imageCount, Modifier,
                    { callback?.onBack() })
                { callback?.onMenuClick(true) }
                
                1 -> state.load?.let {
                    HiddenPhotoAppBar(it)
                    { callback?.onBack() }
                }
                
                2 -> PhotoAppBar(
                    state.imageCount, Modifier,
                    { callback?.onBack() })
                
                else -> {}
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
    ) {
        ZoomableAsyncImage(
            state.image, Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
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
            GKebabButton(
                Modifier.padding(16.dp)
            ) { it() }
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

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ZoomableAsyncImage(
    model: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Transparent,
    imageAlign: Alignment = Center,
    shape: Shape = RectangleShape,
    maxScale: Float = 1f,
    minScale: Float = 5f,
    contentScale: ContentScale = Fit,
    isRotation: Boolean = false,
    isZoomable: Boolean = true,
    scrollState: ScrollableState? = null
) {
    val scope =
        rememberCoroutineScope()
    var scale by remember {
        mutableStateOf(1f)
    }
    var rotationState by remember {
        mutableStateOf(1f)
    }
    var offsetX by remember {
        mutableStateOf(1f)
    }
    var offsetY by remember {
        mutableStateOf(1f)
    }
    Box(
        Modifier
            .clip(shape)
            .background(backgroundColor)
            .combinedClickable(
                MutableInteractionSource(),
                (null), onClick = {},
                onDoubleClick = {
                    if(scale >= 2f) {
                        scale = 1f
                        offsetX = 1f
                        offsetY = 1f
                    } else scale = 3f
                },
            )
            .pointerInput(Unit) {
                if(isZoomable) {
                    forEachGesture {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                                scale *= event.calculateZoom()
                                if(scale > 1) {
                                    scrollState?.run {
                                        scope.launch {
                                            setScrolling(false)
                                        }
                                    }
                                    val offset =
                                        event.calculatePan()
                                    offsetX += offset.x
                                    offsetY += offset.y
                                    rotationState +=
                                        event.calculateRotation()
                                    scrollState?.run {
                                        scope.launch {
                                            setScrolling(true)
                                        }
                                    }
                                } else {
                                    scale = 1f
                                    offsetX = 1f
                                    offsetY = 1f
                                }
                            } while(event.changes.any
                                { it.pressed }
                            )
                        }
                    }
                }
            }
    ) {
        AsyncImage(
            model, (null),
            modifier
                .align(imageAlign)
                .graphicsLayer {
                    if(isZoomable) {
                        scaleX = maxOf(
                            maxScale, minOf(
                                minScale, scale
                            )
                        )
                        scaleY = maxOf(
                            maxScale, minOf(
                                minScale, scale
                            )
                        )
                        if(isRotation)
                            rotationZ = rotationState
                        translationX = offsetX
                        translationY = offsetY
                    }
                },
            contentScale = contentScale,
        )
    }
}

suspend fun ScrollableState.setScrolling(
    value: Boolean
) {
    scroll(PreventUserInput) {
        if(value) Unit
        else awaitCancellation()
    }
}