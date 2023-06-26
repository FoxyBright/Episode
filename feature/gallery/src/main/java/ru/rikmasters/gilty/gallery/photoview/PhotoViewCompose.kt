package ru.rikmasters.gilty.gallery.photoview

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.LOAD
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTOS
import ru.rikmasters.gilty.gallery.photoview.PhotoViewType.PHOTO
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_back
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.shared.*
import ru.rikmasters.gilty.shared.theme.Colors
import ru.rikmasters.gilty.shared.theme.Gradients.red
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
import kotlin.collections.set
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
            (false), PHOTOS
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

enum class PhotoViewType { PHOTOS, PHOTO, LOAD }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun PhotoView(
    images: List<AvatarModel?>,
    selected: AvatarModel?,
    menuState: Boolean,
    type: PhotoViewType,
    modifier: Modifier = Modifier,
    loadSeconds: Int = 7000,
    onMenuClick: ((Boolean) -> Unit)? = null,
    menuItems: List<Pair<String, (AvatarModel?) -> Unit>> = listOf(),
    back: Color = colorScheme.primaryContainer,
    onBack: (() -> Unit)? = null,
) {
    if (selected == null && images.isEmpty()) return
    val selectedImage = selected ?: images.first()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var timer by remember { mutableStateOf(false) }

    var mapOfDownloadedImages by remember { mutableStateOf(mapOf<Int, Boolean>()) }

    val select by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    val animateTimer = remember { Animatable(0f) }
    val asm = get<AppStateModel>()

    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            asm.systemUi.setStatusBarColor(back)
            asm.systemUi.setSystemBarsColor(back)
            asm.systemUi.setNavigationBarColor(back)
        }
    })

    LaunchedEffect(Unit) {
        if (type == LOAD) timer = true

        images.forEach {
            it?.let {
                if (it.id == selectedImage?.id)
                    listState.scrollToItem(
                        images.indexOf(it)
                    )
            }
        }
    }

    LaunchedEffect(key1 = select, key2 = mapOfDownloadedImages, block = {
        if (!timer) return@LaunchedEffect
        if (mapOfDownloadedImages[select] == true) {
            scope.launch { animateTimer.animateTo(1f, tween(loadSeconds)) }
        } else {
            val map = mapOfDownloadedImages.toMutableMap()
            map[select] = false
            mapOfDownloadedImages = map
        }
    })

    LaunchedEffect(key1 = animateTimer.value, block = {
        if (!timer) return@LaunchedEffect
        if (animateTimer.value + 0.001f >= 1f) {
            scope.launch {
                if (select != images.lastIndex) {
                    animateTimer.snapTo(0f)
                    listState.animateScrollToItem(select + 1)
                } else onBack?.let { it() }
            }
        }
    })

    val counter = "${select + 1}/${images.size}"

    val screenWidth = METRICS.widthPixels / METRICS.density

    Popup {

        BackHandler { onBack?.let { it() } }

        GDropMenu(
            menuState = menuState,
            collapse = { onMenuClick?.let { it(false) } },
            offset = DpOffset(
                x = (screenWidth - 160).dp,
                y = -(100).dp
            ),
            menuItems = menuItems.map {
                it.first to {
                    it.second.invoke(images[select])
                }
            },
        )

        Scaffold(
            modifier = modifier.background(
                Colors.Black
            ), topBar = {

                when (type) {
                    PHOTOS -> {
                        asm.systemUi.setStatusBarColor(Colors.PreDark)
                        asm.systemUi.setSystemBarsColor(Colors.PreDark)
                        PhotosAppBar(
                            text = counter,
                            modifier = Modifier,
                            onBack = { onBack?.let { it() } },
                            onMenuClick = onMenuClick.let { { it?.let { it1 -> it1(true) } } }
                                ?: run { null }
                        )
                    }

                    PHOTO -> {
                        asm.systemUi.setStatusBarColor(Colors.PreDark)
                        asm.systemUi.setSystemBarsColor(Colors.PreDark)
                        PhotoAppBar(
                            modifier = Modifier,
                            onBack = { onBack?.let { it() } },
                            onMenuClick = onMenuClick?.let { { it(true) } }
                        )
                    }

                    LOAD -> {
                        asm.systemUi.setStatusBarColor(Colors.Black)
                        asm.systemUi.setSystemBarsColor(Colors.Black)
                        HiddenPhotoAppBar(
                            load = animateTimer.value,
                            counter = counter,
                        ) { onBack?.let { it() } }

                    }
                }
            }
        ) {
            val offset = remember {
                derivedStateOf {
                    listState.firstVisibleItemScrollOffset
                }
            }.value

            if (!listState.isScrollInProgress)
                scope.scrollBasic(listState, offset <= 250)

            var scrollState by remember {
                mutableStateOf(true)
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red),
                state = listState,
                userScrollEnabled = if (type == LOAD) false else scrollState
            ) {
                itemsIndexed(images) { index, photo ->
                    ZoomableAsyncImage(
                        model = photo?.thumbnail?.url,
                        modifier = Modifier
                            .width(screenWidth.dp)
                            .fillMaxHeight(),
                        backgroundColor = Colors.Black,
                        scrollDisable = {
                            scrollState = it
                        },
                        onImageLoaded = {
                            val map = mapOfDownloadedImages.toMutableMap()
                            map[index] = true
                            mapOfDownloadedImages = map
                        }
                    )
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
            if (left) listState.firstVisibleItemIndex
            else listState.firstVisibleItemIndex + 1
        )
    }
}

@Composable
private fun PhotosAppBar(
    text: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onMenuClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Colors.PreDark),
        //.background(colorScheme.primaryContainer),
        SpaceBetween, CenterVertically
    ) {
        Row(Modifier, Start, CenterVertically) {
            Back(Modifier.padding(16.dp), tint = Colors.WhiteOnSurface, onBack)
            Text(
                text = text,
                modifier = Modifier.padding(),
                color = Colors.WhiteOnSurface,
                //color = Colors.White, colorScheme.tertiary,
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
private fun PhotoAppBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onMenuClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            //    .background(colorScheme.primaryContainer)
            .background(Colors.PreDark),
        SpaceBetween, CenterVertically
    ) {
        Row(Modifier, Start, CenterVertically) {
            Back(modifier = Modifier.padding(16.dp), tint = Colors.WhiteOnSurface, onBack)
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
    counter: String,
    onBack: () -> Unit,
) {
    Column(modifier) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Back(modifier = Modifier.padding(top = 24.dp), tint = Colors.White, onClick = onBack)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Center
            ) {
                Text(
                    text = counter, color = Colors.White, style = typography.headlineLarge
                )
            }
        }
        Loader(
            load = load, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun Back(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit,
) {
    IconButton(onClick, modifier) {
        Icon(
            painterResource(ic_back),
            (null), Modifier.size(24.dp), tint = tint
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
    onImageLoaded: () -> Unit,
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
                    if (scale >= 2f) {
                        scale = 1f
                        offsetX = 1f
                        offsetY = 1f
                    } else scale = 3f
                },
            )
            .pointerInput(Unit) {
                if (isZoomable) {
                    forEachGesture {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                                scale *= event.calculateZoom()
                                if (scale > 1) {
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
                            } while (event.changes.any
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
                .background(backgroundColor),
            Center
        ) {
            AsyncImage(
                model, (null),
                modifier
                    .align(imageAlign)
                    .graphicsLayer {
                        if (isZoomable) {
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
                            if (isRotation)
                                rotationZ = rotationState
                            translationX = offsetX
                            translationY = offsetY
                        }
                    },
                contentScale = contentScale,
                onLoading = { placeholder = true },
                onSuccess = {
                    placeholder = false
                    onImageLoaded()
                },
            )
            if (placeholder) AnimatedImage(R.raw.loaging)
        }
    }
}

suspend fun ScrollableState.setScrolling(
    value: Boolean,
) {
    scroll(PreventUserInput) {
        if (value) Unit
        else awaitCancellation()
    }
}