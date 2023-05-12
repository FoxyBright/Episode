package ru.rikmasters.gilty.presentation.ui

import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.viewmodel.TranslationViewModel

@Composable
fun TestTranslationScreen(
    vm: TranslationViewModel
) {
    val translationScreenState by vm.translationUiState.collectAsState()

    var camera: RtmpCamera2? = null
    var streamState by remember { mutableStateOf(StreamState.STOP) }

    fun startBroadCast(rtmpUrl: String) {
        camera?.let {
            if (!it.isStreaming) {
                if (it.prepareAudio() && it.prepareVideo()) {
                    streamState = StreamState.PLAY
                    it.startStream(rtmpUrl)
                } else {
                    streamState = StreamState.STOP
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val view = OpenGlView(it)
                view.keepScreenOn = true
                view.isKeepAspectRatio = true
                view
            },
            update = {
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
                camera = RtmpCamera2(it, connectionChecker)
                val surfaceHolderCallback = object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        camera?.startPreview()
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int,
                    ) {
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        camera?.stopPreview()
                    }
                }
                it.holder.addCallback(surfaceHolderCallback)
            },
        )
        Button(
            onClick = {
                translationScreenState.translationInfo?.let {
                    Log.d("TEST","RTMP ${it.rtmp} RTMPHOST ${it.rtmpHost}")
                    startBroadCast(
                        rtmpUrl = it.rtmp
                    )
                }
            },
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        ) {
            Text(
                text = "Начать тестовую трансляцию",
            )
        }
    }
}

private enum class StreamState {
    PLAY, STOP
}
