package com.example.translation.presentation.ui

import android.view.LayoutInflater
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
import ru.rikmasters.gilty.feature.translation.R

@Composable
fun TestTranslationScreen() {
    var streamState by remember { mutableStateOf(StreamState.STOP) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val view = LayoutInflater.from(
                    it,
                ).inflate(R.layout.open_gl_view, (null), false)
                    .findViewById<OpenGlView>(R.id.openGlView)
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
                val rtmpCamera = RtmpCamera2(view, connectionChecker)
                val surfaceHolderCallback = object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        rtmpCamera.startPreview()
                    }
                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int,
                    ) {
                    }
                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        rtmpCamera.stopPreview()
                    }
                }
                view.holder.addCallback(surfaceHolderCallback)

                view
            },
        )
        Button(
            onClick = {
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
