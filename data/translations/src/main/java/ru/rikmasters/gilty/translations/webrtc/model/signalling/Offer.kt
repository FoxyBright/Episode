package ru.rikmasters.gilty.translations.webrtc.model.signalling

import org.webrtc.IceCandidate
import org.webrtc.PeerConnection.IceServer
import org.webrtc.SessionDescription
import ru.rikmasters.gilty.translations.webrtc.utils.asListOfType
import ru.rikmasters.gilty.translations.webrtc.utils.asMapOfKeysType

data class Offer(
    val id: Int,
    val sdp: String,
    val candidates: List<Candidate>,
    val iceServers: List<IceServers>
)

fun Map<String, *>.mapToOffer(): Offer {
    val id = this["id"] as? Int ?: 0
    val sdp = (this["sdp"] as? Map<*,*>)?.let {
        it.asMapOfKeysType<String>()?.let {
            it["sdp"] as? String ?: ""
        } ?: ""
    } ?: ""
    val candidates = (this["candidates"] as? List<*>)?.asListOfType<Map<String, *>>()?.let {
        it.map { candidatesJson ->
            candidatesJson.mapToCandidate()
        }
    } ?: emptyList()
    val iceServers = (this["iceServers"] as? List<*>)?.asListOfType<Map<String, *>>()?.let {
        it.map { iceServersJson ->
            iceServersJson.mapToIceServers()
        }
    } ?: emptyList()
    return Offer(
        id = id,
        sdp = sdp,
        candidates = candidates,
        iceServers = iceServers
    )
}

data class RealOffer(
    val sdp: SessionDescription,
    val iceServers: List<IceServer>,
    val iceCandidates: List<IceCandidate>
)

fun Offer.mapToRealOffer(): RealOffer {
    return RealOffer(
        sdp = SessionDescription(
            SessionDescription.Type.OFFER,
            sdp
        ),
        iceServers = iceServers.map {
            IceServer.builder(it.urls).setUsername(it.user).setPassword(it.credential).createIceServer()
        },
        iceCandidates = candidates.map {
            IceCandidate(
                it.sdpMid,
                it.sdpMLineIndex,
                it.sdp
            )
        }
    )
}