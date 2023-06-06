package ru.rikmasters.gilty.translations.webrtc.model.signalling

data class Candidate(
    val sdp: String,
    val sdpMid: String,
    val sdpMLineIndex: Int
)

fun Map<String, *>.mapToCandidate(): Candidate {
    val sdp = this["candidate"] as? String ?: ""
    val sdpMid = this["sdpMid"] as? String ?: ""
    val sdpMLineIndex = this["sdpMLineIndex"] as? Int ?: 0
    return Candidate(
        sdp = sdp,
        sdpMid = sdpMid,
        sdpMLineIndex = sdpMLineIndex
    )
}
