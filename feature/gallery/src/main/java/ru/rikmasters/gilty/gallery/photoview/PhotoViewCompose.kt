package ru.rikmasters.gilty.gallery.photoview

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.MutatePriority.PreventUserInput
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.LOAD
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import kotlin.math.*

@Preview
@Composable
private fun PhotoPreview() {
    GiltyTheme {
        PhotoView(
            listOf(
                DemoAvatarModel,
                DemoAvatarModel,
                DemoAvatarModel,
                DemoAvatarModel,
            ),
            DemoAvatarModel,
            (false), PHOTO
        )
    }
}

@Preview
@Composable
private fun HiddenPhotoPreview() {
    GiltyTheme {
        PhotoView(
            listOf(
                DemoAvatarModel,
                DemoAvatarModel,
                DemoAvatarModel,
                DemoAvatarModel,
            ),
            DemoAvatarModel,
            (false), LOAD, Modifier,
        )
    }
}

enum class PhotoViewType { PHOTO, LOAD }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun PhotoView(
    images: List<AvatarModel?>,
    selected: AvatarModel?,
    menuState: Boolean,
    type: PhotoViewType,
    modifier: Modifier = Modifier,
    loadSeconds: Int = 6000,
    onMenuClick: ((Boolean) -> Unit)? = null,
    onMenuItemClick: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    if(selected == null && images.isEmpty()) return
    val selectedImage = selected ?: images.first()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var timer by remember {
        mutableStateOf(false)
    }
    
    LaunchedEffect(Unit) {
        if(type == LOAD) timer = true
        images.forEach {
            it?.let {
                if(it.id == selectedImage?.id)
                    listState.scrollToItem(
                        images.indexOf(it)
                    )
            }
        }
    }
    
    val select by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    
    val animateTimer = animateFloatAsState(
        if(timer) 1f else 0f, tween(loadSeconds),
        label = ""
    ) {
        scope.launch {
            if(select != images.lastIndex) {
                listState.animateScrollToItem(select + 1)
                // TODO - обновление таймера нужно сделать
            } else onBack?.let { it() }
        }
    }.value
    
    val counter = "${select + 1}/${images.size}"
    
    val screenWidth = METRICS.widthPixels / METRICS.density
    
    Popup {
        BackHandler { onBack?.let { it() } }
        Scaffold(
            modifier.background(
                colorScheme.background
            ), {
                when(type) {
                    PHOTO -> PhotoAppBar(
                        counter, Modifier,
                        { onBack?.let { it() } }
                    )
                    
                    LOAD -> HiddenPhotoAppBar(animateTimer)
                    { onBack?.let { it() } }
                    
                }
                GDropMenu(
                    menuState, { onMenuClick?.let { it(false) } },
                    DpOffset((screenWidth - 160).dp, -(100).dp),
                    listOf(
                        Pair(stringResource(R.string.edit_button)) {
                            onMenuItemClick?.let {
                                it(images[select]?.id!!)
                            }
                        },
                    )
                )
            }
        ) {
            val offset = remember {
                derivedStateOf {
                    listState.firstVisibleItemScrollOffset
                }
            }.value
            
            if(!listState.isScrollInProgress)
                scope.scrollBasic(listState, offset <= 250)
            
            var scrollState by remember {
                mutableStateOf(true)
            }
            LazyRow(
                Modifier
                    .fillMaxSize()
                    .background(Color.Red),
                listState,
                userScrollEnabled = scrollState
            ) {
                items(images) { photo ->
                    ZoomableAsyncImage(
                        photo?.url, Modifier
                            .width(screenWidth.dp)
                            .fillMaxHeight()
                    ) { scrollState = it }
                }
            }
        }
    }
}

private fun CoroutineScope.scrollBasic(
    listState: LazyListState,
    left: Boolean = false,
) {
    launch {
        listState.animateScrollToItem(
            if(left) listState.firstVisibleItemIndex
            else listState.firstVisibleItemIndex + 1
        )
    }
}

@Composable
private fun PhotoAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onMenuClick: (() -> Unit)? = null,
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
    onBack: () -> Unit,
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
    onClick: () -> Unit,
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
    modifier: Modifier = Modifier,
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
@Suppress("deprecation")
@OptIn(ExperimentalFoundationApi::class)
fun ZoomableAsyncImage(
    model: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Transparent,
    imageAlign: Alignment = Center,
    shape: Shape = RectangleShape,
    maxScale: Float = 0.5f,
    minScale: Float = 5f,
    contentScale: ContentScale = Fit,
    isRotation: Boolean = false,
    isZoomable: Boolean = true,
    scrollState: ScrollableState? = null,
    scrollDisable: (Boolean) -> Unit,
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
                                    scrollDisable(false)
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
                                    scrollDisable(true)
                                }
                            } while(event.changes.any
                                { it.pressed }
                            )
                        }
                    }
                }
            }
    ) {
        var placeholder by remember {
            mutableStateOf(true)
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background),
            Center
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
                onLoading = { placeholder = true },
                onSuccess = { placeholder = false },
            )
            if(placeholder) AnimatedImage(R.raw.loaging)
        }
    }
}

suspend fun ScrollableState.setScrolling(
    value: Boolean,
) {
    scroll(PreventUserInput) {
        if(value) Unit
        else awaitCancellation()
    }
}