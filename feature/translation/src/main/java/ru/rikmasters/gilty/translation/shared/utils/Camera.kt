package ru.rikmasters.gilty.translation.shared.utils

import android.content.Context
import android.util.Size
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing
import ru.rikmasters.gilty.translation.streamer.model.SurfaceState

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
            val realOrientation = CameraHelper.getCameraOrientation(context)
            if (it.prepareAudio() && it.prepareVideo(
                    1280,
                    720,
                    30,
                    3_000_000,
                    realOrientation
                )) {
                it.startStream(rtmpUrl)
            }
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