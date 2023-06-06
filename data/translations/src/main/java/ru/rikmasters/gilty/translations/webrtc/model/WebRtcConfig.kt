package ru.rikmasters.gilty.translations.webrtc.model

data class WebRtcConfig(
    val wssUrl: String,
    val retryEnable: Boolean = true,
    val retryCount: Int = 10,
    // TODO: ios time interval
    val retryInterval: Double = 0.5
)