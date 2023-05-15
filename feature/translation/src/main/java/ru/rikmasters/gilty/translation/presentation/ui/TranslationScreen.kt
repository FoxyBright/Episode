package ru.rikmasters.gilty.translation.presentation.ui

import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.AspectRatioMode
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.Gradients.green
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState

@Composable
fun TranslationScreen(
    translationStatus: TranslationStatus,
    onCloseClicked: () -> Unit,
    translationUiState: TranslationUiState,
    changeFacing: () -> Unit
) {
    var camera by remember { mutableStateOf<RtmpCamera2?>(null) }

    camera?.let {
        if (
            translationUiState.selectedCamera == Facing.FRONT && it.cameraFacing != CameraHelper.Facing.FRONT
            || translationUiState.selectedCamera == Facing.BACK && it.cameraFacing != CameraHelper.Facing.BACK
        ) {
            it.switchCamera()
        }
    }

    var streamState by remember { mutableStateOf(StreamState.STOP) }

    val connectionChecker = object : ConnectCheckerRtmp {
        override fun onAuthErrorRtmp() {}
        override fun onAuthSuccessRtmp() {}
        override fun onConnectionFailedRtmp(reason: String) {}
        override fun onConnectionStartedRtmp(rtmpUrl: String) {}
        override fun onConnectionSuccessRtmp() {
            streamState = StreamState.PLAY
        }

        override fun onDisconnectRtmp() {
            streamState = StreamState.STOP
        }

        override fun onNewBitrateRtmp(bitrate: Long) {}
    }

    fun startBroadCast(rtmpUrl: String) {
        camera?.let {
            if (!it.isStreaming) {
                if (it.prepareAudio() && it.prepareVideo()) {
                    streamState = StreamState.PLAY
                    it.startStream(rtmpUrl)
                } else {
                    streamState = StreamState.STOP
                }
            } else {
                it.stopStream()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = if (translationStatus == TranslationStatus.PREVIEW) {
                RoundedCornerShape(
                    topEnd = 14.dp,
                    topStart = 14.dp
                )
            } else {
                RoundedCornerShape(14.dp)
            },
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        val view = OpenGlView(it)
                        view.keepScreenOn = true
                        view.isKeepAspectRatio = true
                        view.setAspectRatioMode(AspectRatioMode.Fill)
                        view.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        val surfaceHolderCallback = object : SurfaceHolder.Callback {
                            override fun surfaceCreated(holder: SurfaceHolder) {}
                            override fun surfaceChanged(
                                holder: SurfaceHolder,
                                format: Int,
                                width: Int,
                                height: Int,
                            ) {
                                camera?.startPreview()
                            }

                            override fun surfaceDestroyed(holder: SurfaceHolder) {
                                camera?.stopPreview()
                            }
                        }
                        view.holder.addCallback(surfaceHolderCallback)
                        camera = RtmpCamera2(view, connectionChecker)
                        view
                    },
                    update = {}
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier
                                .height(5.dp)
                                .width(40.dp)
                                .align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(11.dp),
                            color = MaterialTheme.colorScheme.onTertiary
                        ) {}
                        Spacer(modifier = Modifier.height(22.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.translations_preview_title),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.headlineLarge
                            )
                            IconButton(onClick = onCloseClicked) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Close preview"
                                )
                            }
                        }
                    }
                    if (translationStatus == TranslationStatus.PREVIEW) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .clickable {
                                            changeFacing()
                                        },
                                    color = if (translationUiState.selectedCamera == Facing.FRONT) {
                                        MaterialTheme.colorScheme.scrim
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(
                                            vertical = 6.dp,
                                            horizontal = 16.dp
                                        ),
                                        text = stringResource(id = R.string.translations_front_camera),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .clickable {
                                            changeFacing()
                                        },
                                    color = if (translationUiState.selectedCamera == Facing.BACK) {
                                        MaterialTheme.colorScheme.scrim
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(
                                            vertical = 6.dp,
                                            horizontal = 16.dp
                                        ),
                                        text = stringResource(id = R.string.translations_main_camera),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier
                                    .clickable {
                                        translationUiState.translationInfo?.let {
                                            startBroadCast(
                                                rtmpUrl = it.rtmp
                                            )
                                        }
                                    }
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(green()),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(vertical = 16.dp),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.linearGradient(green())
                                        ),
                                    text = stringResource(id = R.string.translations_start_strean),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(53.dp))
                        }
                    }
                }
            }
        }
    }
}

private enum class StreamState {
    STOP, PLAY
}