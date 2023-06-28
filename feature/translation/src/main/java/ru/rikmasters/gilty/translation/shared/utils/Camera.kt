package ru.rikmasters.gilty.translation.shared.utils

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.pedro.encoder.input.gl.render.filters.`object`.ImageObjectFilterRender
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.OpenGlView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.shared.common.extentions.blur
import ru.rikmasters.gilty.shared.common.extentions.getBitmap
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

fun restartPreview(camera: RtmpCamera2?, facing: StreamerFacing, context: Context) {
    camera?.let {
        if (it.isOnPreview) {
            it.stopPreview()
        }
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

fun startBroadCast(rtmpUrl: String, camera: RtmpCamera2?, context: Context, facing: StreamerFacing) {
    camera?.let {
        if (!it.isStreaming) {
            val realOrientation = CameraHelper.getCameraOrientation(context)
            if (!it.isOnPreview) {
                it.startPreview(
                    facing.map(),
                    1280,
                    720,
                    realOrientation
                )
            }
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


fun restartBroadCast(
    rtmpUrl: String,
    camera: RtmpCamera2?,
    context: Context,
    cameraState: Boolean,
    currentOpenGlView: OpenGlView?,
    thumbnailUrl: String?,
    scope: CoroutineScope,
    microphoneState: Boolean,
    facing: StreamerFacing
) {
    camera?.let {
        if (it.isStreaming) {
            it.stopStream()
            it.stopPreview()
        }
        val realOrientation = CameraHelper.getCameraOrientation(context)
        it.startPreview(
            facing.map(),
            1280,
            720,
            realOrientation
        )
        if (it.prepareAudio() && it.prepareVideo(
                1280,
                720,
                30,
                3_000_000,
                realOrientation
            )
        ) {
            it.startStream(rtmpUrl)
            scope.launch {
                //TODO: Костыль
                delay(500)
                toggleCamera(cameraState, it, context, currentOpenGlView, thumbnailUrl, scope)
                toggleMicrophone(microphoneState, it)
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

fun toggleCamera(
    value: Boolean,
    camera: RtmpCamera2?,
    context: Context,
    currentOpenGlView: OpenGlView?,
    thumbnailUrl: String?,
    scope: CoroutineScope
) {
    if (value) {
        camera?.glInterface?.clearFilters()
    } else {
        val imageFilter = ImageObjectFilterRender()
        currentOpenGlView?.getBitmap { bitmap ->
            bitmap?.let {
                imageFilter.setImage(it.blur(context))
                camera?.glInterface?.addFilter(imageFilter)
            } ?: run {
                thumbnailUrl?.let {
                    scope.launch {
                        val loader = ImageLoader(context)
                        val request = ImageRequest.Builder(context)
                            .data(thumbnailUrl)
                            .build()
                        val result = (loader.execute(request)).drawable?.toBitmap()
                        result?.let { btmp ->
                            imageFilter.setImage(btmp.blur(context))
                            camera?.glInterface?.addFilter(imageFilter)
                        } ?: kotlin.run {
                            camera?.glInterface?.muteVideo()
                        }
                    }
                } ?: {
                    camera?.glInterface?.muteVideo()
                }
            }
        } ?: run {
            thumbnailUrl?.let {
                scope.launch {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(thumbnailUrl)
                        .allowHardware(false)
                        .build()
                    val result = (loader.execute(request) as? SuccessResult)?.drawable
                    result?.let { btmp ->
                        imageFilter.setImage((btmp as BitmapDrawable).bitmap.blur(context))
                    } ?: kotlin.run {
                        camera?.glInterface?.muteVideo()
                    }
                }
            } ?: {
                camera?.glInterface?.muteVideo()
            }
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

