package ru.rikmasters.gilty.translations.webrtc.model

import org.webrtc.PeerConnection.IceConnectionState

enum class RTCIceConnectionState {
    count,
    checking,
    closed,
    completed,
    connected,
    disconnected,
    failed,
    new,
    unknow
}

fun IceConnectionState.map() = when(this.name) {
    "count" -> RTCIceConnectionState.count
    "checking" -> RTCIceConnectionState.checking
    "closing" -> RTCIceConnectionState.closed
    "completed" -> RTCIceConnectionState.completed
    "connected" -> RTCIceConnectionState.connected
    "disconnected" -> RTCIceConnectionState.disconnected
    "failed" -> RTCIceConnectionState.failed
    "new" -> RTCIceConnectionState.new
    else -> RTCIceConnectionState.unknow
}