package ru.rikmasters.gilty.translations.model

data class GG(
    val begin_at: String,
    val camera: Boolean,
    val completed_at: String,
    val duration: Int,
    val id: String,
    val is_streaming: Boolean,
    val llhls: String,
    val llhls_host: String,
    val microphone: Boolean,
    val rtmp: String,
    val rtmp_host: String,
    val started_at: String,
    val status: String,
    val thumbnail: String,
    val thumbnail_host: String,
    val user_id: String,
    val was_streaming: Boolean,
    val webrtc: String,
    val webrtc_host: String
)