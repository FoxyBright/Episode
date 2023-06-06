package ru.rikmasters.gilty.shared.models.translations

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.shared.models.enumeration.TranslationStatusDTO

data class TranslationInfoDTO(
    val id: String,
    val userId: String,
    val startedAt: String ?= null,
    val camera: Boolean ?= null,
    val microphone: Boolean ?= null,
    val status: TranslationStatusDTO,
    val beginAt: String,
    val completedAt: String,
    val isStreaming: Boolean,
    val wasStreaming: Boolean,
    val thumbnailHost: String,
    val thumbnail: String,
    val webrtcHost: String,
    val webrtc: String,
    val llhlsHost: String,
    val llhls: String,
    val rtmpHost: String ? = null,
    val rtmp: String ? = null,
) {
    fun map() = TranslationInfoModel(
        id = id,
        userId = userId,
        startedAt = startedAt?.let { LocalDateTime.of(it) },
        camera = camera ?: true,
        microphone = microphone ?: true,
        status = status.map(),
        beginAt = LocalDateTime.of(beginAt),
        completedAt = LocalDateTime.of(completedAt),
        isStreaming = isStreaming,
        wasStreaming = wasStreaming,
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