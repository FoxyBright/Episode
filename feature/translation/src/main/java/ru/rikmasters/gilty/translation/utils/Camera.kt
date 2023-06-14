package ru.rikmasters.gilty.translation.utils

import android.content.Context
import android.util.Log
import android.util.Size
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

fun startPreview(camera: RtmpCamera2?, facing: StreamerFacing, orientation: Int, sensorOrientation: Int) {
    camera?.let {
        if (!it.isOnPreview) {
            val resolutionFront = it.resolutionsFront
            val resolutionBack = it.resolutionsBack
            val isHd = (resolutionBack.contains(Size(1280, 720)) && resolutionFront.contains(
                Size(
                    1280,
                    720
                )
            ))
            val width = if (isHd) 1280 else 640
            val height = if (isHd) 720 else 480
            it.startPreview(
                facing.map(),
                width,
                height,
               // sensorOrientation
            )
        }
    }
}

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