package ru.rikmasters.gilty.shared.model.translations

import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel
import java.time.ZonedDateTime


data class TranslationInfoModel(
    val id: String,
    val userId: String,
    val startedAt: ZonedDateTime ?= null,
    val camera: Boolean,
    val microphone: Boolean,
    val status: TranslationStatusModel,
    val beginAt: ZonedDateTime,
    val completedAt: ZonedDateTime,
    val isStreaming: Boolean,
    val wasStreaming: Boolean,
    val thumbnailHost: String,
    val thumbnail: String,
    val webrtcHost: String,
    val webrtc: String,
    val llhlsHost: String,
    val llhls: String,
    val rtmpHost: String ? = null,
    val rtmp: String ? = null
)