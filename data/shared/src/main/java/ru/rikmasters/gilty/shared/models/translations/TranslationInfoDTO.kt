package ru.rikmasters.gilty.shared.models.translations

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.models.enumeration.TranslationStatusDTO

data class TranslationInfoDTO(
    val id: String,
    val userId: String,
    val startedAt: String,
    val camera: Boolean,
    val microphone: Boolean,
    val status: TranslationStatusDTO,
    val beginAt: String,
    val completedAt: String,
    val isStreaming: String,
    val wasStreaming: String,
    val thumbnailHost: String,
    val thumbnail: String,
    val webrtcHost: String,
    val webrtc: String,
    val llhlsHost: String,
    val llhls: String,
    val rtmpHost: String,
    val rtmp: String,
) {
    fun map() = TranslationInfoModel(
        id = id,
        userId = userId,
        startedAt = LocalDateTime.Companion.of(startedAt),
        camera = camera,
        microphone = microphone,
        status = status.map(),
        beginAt = LocalDateTime.Companion.of(beginAt),
        completedAt = LocalDateTime.Companion.of(completedAt),
        isStreaming = LocalDateTime.Companion.of(isStreaming),
        wasStreaming = LocalDateTime.Companion.of(wasStreaming),
        thumbnailHost = thumbnailHost,
        thumbnail = thumbnail,
        webrtcHost = webrtcHost,
        webrtc = webrtc,
        llhlsHost = llhlsHost,
        llhls = llhls,
        rtmpHost = rtmpHost,
        rtmp = rtmp,
    )
}