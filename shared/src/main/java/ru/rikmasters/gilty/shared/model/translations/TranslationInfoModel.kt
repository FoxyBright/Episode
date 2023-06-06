package ru.rikmasters.gilty.shared.model.translations

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel


data class TranslationInfoModel(
    val id: String,
    val userId: String,
    val startedAt: LocalDateTime ?= null,
    val camera: Boolean,
    val microphone: Boolean,
    val status: TranslationStatusModel,
    val beginAt: LocalDateTime,
    val completedAt: LocalDateTime,
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