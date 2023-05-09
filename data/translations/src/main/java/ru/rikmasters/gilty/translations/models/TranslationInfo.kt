package ru.rikmasters.gilty.translations.models

data class TranslationInfo(
    val id: String,
    val userId: String,
    val startedAt: String,
    val camera: Boolean,
    val microphone: Boolean,
    val status: TranslationStatus,
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
    val rtmp: String
)