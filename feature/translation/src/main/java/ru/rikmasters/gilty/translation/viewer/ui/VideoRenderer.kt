package ru.rikmasters.gilty.translation.viewer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.RendererCommon.RendererEvents
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack


@Composable
fun VideoRenderer(
    videoTrack: VideoTrack,
    modifier: Modifier = Modifier,
    eglBaseContext: EglBase.Context,
    initialize: (SurfaceViewRenderer) -> Unit
) {

    val trackState: MutableState<VideoTrack?> = remember { mutableStateOf(null) }
    var view: SurfaceViewRenderer? by remember { mutableStateOf(null) }


    DisposableEffect(videoTrack) {
        onDispose {
            cleanTrack(view, trackState)
        }
    }

    AndroidView(
        factory = { context ->
            SurfaceViewRenderer(context).apply {
                keepScreenOn = true
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                init(
                    eglBaseContext,
                    object : RendererEvents {
                        override fun onFirstFrameRendered() {}

                        override fun onFrameResolutionChanged(
                            videoWidth: Int,
                            videoHeight: Int,
                            rotation: Int
                        ) {}
                    }
                )
                setupVideo(trackState, videoTrack, this)
                view = this
            }
        },
        update = { v ->
            setupVideo(trackState, videoTrack, v)
            initialize(v)
        },
        modifier = modifier
    )
}

private fun cleanTrack(
    view: SurfaceViewRenderer?,
    trackState: MutableState<VideoTrack?>
) {
    view?.let { trackState.value?.removeSink(it) }
    trackState.value = null
}

private fun setupVideo(
    trackState: MutableState<VideoTrack?>,
    track: VideoTrack,
    renderer: SurfaceViewRenderer
) {
    if (trackState.value == track) {
        return
    }

    cleanTrack(renderer, trackState)

    trackState.value = track
    track.addSink(renderer)
    track.setEnabled(true)
}