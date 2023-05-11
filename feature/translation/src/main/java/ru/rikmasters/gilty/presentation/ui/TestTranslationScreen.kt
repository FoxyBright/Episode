package ru.rikmasters.gilty.presentation.ui

import android.view.SurfaceHolder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.OpenGlView

@Composable
fun TestTranslationScreen() {
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
                startBroadCast(
                    rtmpUrl = //TODO: url
                )
            },
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
