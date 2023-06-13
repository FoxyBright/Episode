package ru.rikmasters.gilty.translation.streamer.utils

import com.pedro.encoder.input.video.CameraHelper
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

fun CameraHelper.Facing.map() = when(this) {
    CameraHelper.Facing.FRONT -> StreamerFacing.FRONT
    CameraHelper.Facing.BACK -> StreamerFacing.BACK
}