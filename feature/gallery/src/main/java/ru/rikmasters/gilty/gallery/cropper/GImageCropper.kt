package ru.rikmasters.gilty.gallery.cropper

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RoundedCornerCropShape
import com.smarttoolfactory.cropper.settings.*
import ru.rikmasters.gilty.gallery.cropper.settings.CropOutlineProperty
import ru.rikmasters.gilty.gallery.cropper.settings.CropProperties
import ru.rikmasters.gilty.gallery.cropper.settings.CropStyle
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable
import ru.rikmasters.gilty.shared.R.string
import ru.rikmasters.gilty.shared.shared.AnimatedImage
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ImageCropperPreview() {
    GiltyTheme {
        ImageCropper(
            GImageCropperState(
                ImageBitmap.imageResource(
                    LocalContext.current.resources,
                    drawable.image_test
                )
            )
        )
    }
}

data class GImageCropperState(
    val image: ImageBitmap,
)

interface GImageCropperCallback {
    
    fun onBack()
    fun onCrop(img: ImageBitmap, list: List<Float>)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun ImageCropper(
    state: GImageCropperState,
    modifier: Modifier = Modifier,
    callback: GImageCropperCallback? = null,
) {
    
    val handleSize = LocalDensity.current.run { 20.dp.toPx() }
    var crop by remember { mutableStateOf(false) }
    var croppedImage by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var isCropping by remember {
        mutableStateOf(false)
    }
    
    Scaffold(
        modifier, topBar = {
            TopBar(Modifier)
            { callback?.onBack() }
        }, bottomBar = {
            GradientButton(
                Modifier
                    .padding(bottom = 48.dp)
                    .padding(horizontal = 16.dp),
                stringResource(string.meeting_filter_complete_button),
            ) { crop = true }
        }) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            Center
        ) {
            ImageCropper(
                Modifier.fillMaxSize(),
                state.image, (null), CropStyle(
                    drawOverlay = false,
                    drawGrid = false,
                    strokeWidth = 2.dp,
                    overlayColor = Color.Gray,
                    handleColor = White,
                    backgroundColor = if(isSystemInDarkTheme())
                        Color(0x99000000)
                    else Color(0x99FFFFFF)
                ), CropProperties(
                    cropType = CropType.Static,
                    handleSize = handleSize,
                    contentScale = ContentScale.Fit,
                    cropOutlineProperty = CropOutlineProperty(
                        OutlineType.RoundedRect,
                        RoundedCornerCropShape(0, "Rect")
                    ), maxZoom = 10f,
                    aspectRatio = AspectRatio(3 / 5f),
                    overlayRatio = 0.5f,
                    pannable = true,
                    fling = false,
                    zoomable = true,
                    rotatable = false
                ), crop = crop,
                onCropStart = { isCropping = true }
            ) { img, rect ->
                croppedImage = img
                isCropping = false
                crop = false
                croppedImage?.let {
                    callback?.onCrop(
                        it, listOf(
                            rect.left,
                            rect.top,
                            rect.width,
                            rect.height,
                        )
                    )
                    croppedImage = null
                }
            }
            Frame()
            if(isCropping)
                Box(Modifier, Center) {
                    AnimatedImage(
                        R.raw.loaging,
                        Modifier.size(24.dp),
                        isPlaying = isCropping
                    )
                }
        }
    }
}

@Composable
private fun Frame() {
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
        Center
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.455f)
                .border(
                    width = 2.dp,
                    color = White,
                    shape = RoundedCornerShape(24.dp)
                ),
            Center
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .border(
                        width = 2.dp,
                        color = White,
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.primaryContainer)
            .padding(top = 16.dp),
        Start, CenterVertically
    ) {
        IconButton(
            onBack,
            Modifier.padding(start = 4.dp)
        ) {
            Icon(
                painterResource(drawable.ic_back),
                (null), Modifier.size(24.dp),
                colorScheme.tertiary
            )
        }
        Text(
            stringResource(string.image_preview_cropper),
            Modifier
                .padding(vertical = 18.dp)
                .padding(start = 4.dp),
            style = typography.headlineLarge,
            fontWeight = Medium,
            color = colorScheme.tertiary
        )
    }
}