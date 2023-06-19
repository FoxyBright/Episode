package ru.rikmasters.gilty.translation.shared.utils

import android.content.Context
import android.util.Log
import com.pedro.encoder.input.gl.render.filters.`object`.ImageObjectFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.OpenGlView
import ru.rikmasters.gilty.shared.common.extentions.blur
import ru.rikmasters.gilty.shared.common.extentions.getBitmap
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

fun restartPreview(camera: RtmpCamera2?, facing: StreamerFacing, context: Context) {
    camera?.let {
        if (it.isOnPreview) {
            it.stopPreview()
        }
        // TODO: Подумать над качеством
        val realOrientation = CameraHelper.getCameraOrientation(context)
        it.startPreview(
            facing.map(),
            1280,
            720,
            realOrientation
        )
    }
}

fun stopPreview(camera: RtmpCamera2?) {
    camera?.let {
        if (it.isOnPreview) {
            it.stopPreview()
        }
    }
}

fun startBroadCast(rtmpUrl: String, camera: RtmpCamera2?, context: Context) {
    camera?.let {
        if (!it.isStreaming) {
            Log.d("TEST","STGTTATATA")
            val realOrientation = CameraHelper.getCameraOrientation(context)
            if (it.prepareAudio() && it.prepareVideo(
                    1280,
                    720,
                    30,
                    3_000_000,
                    realOrientation
                )
            ) {
                it.startStream(rtmpUrl)
            }
        }
    }
}


fun restartBroadCast(rtmpUrl: String, camera: RtmpCamera2?, context: Context, facing: StreamerFacing) {
    camera?.let {
        if (it.isStreaming) {
            it.stopStream()
        }
        val realOrientation = CameraHelper.getCameraOrientation(context)
        if (it.prepareAudio() && it.prepareVideo(
                1280,
                720,
                30,
                3_000_000,
                realOrientation
            )
        ) {
            it.startStream(rtmpUrl)
        }
    }
}

fun stopBroadcast(camera: RtmpCamera2?) {
    camera?.let {
        if (it.isStreaming) {
            it.stopStream()
            it.stopPreview()
        }
    }
}

fun destroyRTMP(camera: RtmpCamera2?) {
    camera?.let {
        if (it.isStreaming) {
            it.stopStream()
            it.stopPreview()
        } else {
            it.stopPreview()
        }
    }
}

fun toggleCamera(value: Boolean, camera: RtmpCamera2?, context: Context, currentOpenGlView: OpenGlView?) {
    if (value) {
        camera?.glInterface?.clearFilters()
    } else {
        val imageFilter = ImageObjectFilterRender()
        currentOpenGlView?.getBitmap {
            it?.let { bitmap ->
                imageFilter.setImage(bitmap.blur(context))
                camera?.glInterface?.addFilter(imageFilter)
            } ?: run {
                camera?.glInterface?.muteVideo()
            }
        } ?: run {
            camera?.glInterface?.muteVideo()
        }
    }
}

fun toggleMicrophone(value: Boolean, camera: RtmpCamera2?) {
    if (value) {
        if (camera?.isAudioMuted == true) {
            camera.enableAudio()
        }
    } else {
        if (camera?.isAudioMuted == false) {
            camera.disableAudio()
        }
    }
}

