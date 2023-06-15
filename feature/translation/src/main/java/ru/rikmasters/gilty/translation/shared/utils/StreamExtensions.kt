package ru.rikmasters.gilty.translation.shared.utils

import com.pedro.encoder.input.video.CameraHelper
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

fun StreamerFacing.toggle() : StreamerFacing = when(this) {
    StreamerFacing.FRONT -> StreamerFacing.BACK
    StreamerFacing.BACK -> StreamerFacing.FRONT
}

fun StreamerFacing.map() : CameraHelper.Facing = when(this) {
    StreamerFacing.FRONT -> CameraHelper.Facing.FRONT
    StreamerFacing.BACK -> CameraHelper.Facing.BACK
}

fun CameraHelper.Facing.map() = when(this) {
    CameraHelper.Facing.FRONT -> StreamerFacing.FRONT
    CameraHelper.Facing.BACK -> StreamerFacing.BACK
}