@file:Suppress("DEPRECATION")

package ru.rikmasters.gilty.gallery.cropper

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.core.util.composable.getDensity
import ru.rikmasters.gilty.gallery.cropper.model.AspectRatio
import ru.rikmasters.gilty.gallery.cropper.model.OutlineType.RoundedRect
import ru.rikmasters.gilty.gallery.cropper.model.RoundedCornerCropShape
import ru.rikmasters.gilty.gallery.cropper.settings.*
import ru.rikmasters.gilty.gallery.cropper.settings.CropType.Static
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton
import ru.rikmasters.gilty.shared.theme.Colors.PreDark
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun ImageCropperPreview() {
    GiltyTheme {
        ImageCropper(
            GImageCropperState(
                ImageBitmap.imageResource(
                    LocalContext.current.resources,
                    R.drawable.test_image
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
    val handleSize = getDensity().let {
        remember { with(it) { 20.dp.toPx() } }
    }
    var crop by remember {
        mutableStateOf(false)
    }
    var croppedImage by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var isCropping by remember {
        mutableStateOf(false)
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(Modifier) {
                callback?.onBack()
            }
        },
        bottomBar = {
            GradientButton(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .padding(horizontal = 16.dp),
                text = stringResource(
                    R.string.meeting_filter_complete_button
                ),
            ) { crop = true }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            Center
        ) {
            ImageCropper(
                modifier = Modifier.fillMaxSize(),
                imageBitmap = state.image,
                contentDescription = (null),
                cropStyle = CropStyle(
                    drawOverlay = false,
                    drawGrid = false,
                    strokeWidth = 2.dp,
                    overlayColor = Color.Gray,
                    handleColor = White,
                    backgroundColor = Color(0x80000000)
                ),
                cropProperties = CropProperties(
                    cropType = Static,
                    handleSize = handleSize,
                    contentScale = Fit,
                    cropOutlineProperty = CropOutlineProperty(
                        RoundedRect,
                        RoundedCornerCropShape(0, "Rect")
                    ),
                    maxZoom = 10f,
                    aspectRatio = AspectRatio(3 / 5f),
                    overlayRatio = 0.5f,
                    pannable = true,
                    fling = false,
                    zoomable = true,
                    rotatable = false
                ),
                crop = crop,
                onCropStart = { isCropping = true }
            ) { img, rect ->
                croppedImage = img
                isCropping = false
                crop = false
                croppedImage?.let {
                    callback?.onCrop(
                        state.image, listOf(
                            rect.left, rect.top,
                            rect.width, rect.height,
                        )
                    )
                    croppedImage = null
                }
            }
            Frame()
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
                .fillMaxHeight(0.454f)
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
            .background(PreDark),
        Start, CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 4.dp)
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_back
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = White
            )
        }
        Text(
            text = stringResource(R.string.image_preview_cropper),
            modifier = Modifier
                .padding(vertical = 18.dp)
                .padding(start = 4.dp),
            style = typography.headlineLarge.copy(
                color = White,
                fontWeight = Medium
            ),
        )
    }
}