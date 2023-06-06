package ru.rikmasters.gilty.translations.webrtc.model.signalling
data class SdpAnswer(
    val type:String = "answer",
    val sdp: String
)