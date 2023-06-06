package ru.rikmasters.gilty.translations.webrtc

import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpTransceiver
import ru.rikmasters.gilty.translations.webrtc.model.RTCIceConnectionState
import ru.rikmasters.gilty.translations.webrtc.model.map
import ru.rikmasters.gilty.translations.webrtc.utils.webRtcLog

class PeerConnectionObserver(
    private val onDataChannelChanged: (DataChannel) -> Unit,
    private val onIceConnectionStateChanged: (RTCIceConnectionState) -> Unit,
    private val onIceCandidateRemoved: () -> Unit,
    private val onVideoTrack: (RtpTransceiver) -> Unit
) : PeerConnection.Observer {
    override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
        webRtcLog("[onSignalingChange] #sfu;  newState: $newState")
    }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        webRtcLog("[onIceConnectionChange] #sfu; newState: $newState")
        newState?.let {
            onIceConnectionStateChanged(it.map())
        }
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
        webRtcLog("[onIceConnectionReceivingChange] #sfu; receiving: $receiving")
    }

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
        webRtcLog("[onIceGatheringChange] #sfu; newState: $newState")
    }

    override fun onIceCandidate(candidate: IceCandidate?) {
        webRtcLog("[onIceCandidate] #sfu; candidate: $candidate")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<out IceCandidate>?) {
        webRtcLog("[onIceCandidatesRemoved] #sfu; iceCandidates: $iceCandidates")
        iceCandidates?.let {
            onIceCandidateRemoved()
        }
    }

    override fun onAddStream(stream: MediaStream?) {
        webRtcLog("[onStreamAdded] #sfu; stream: $stream")
    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        webRtcLog("[onTrack] #sfu; tranciever: $transceiver")
        transceiver?.let {
            onVideoTrack(transceiver)
        }
    }

    override fun onRemoveStream(stream: MediaStream?) {
        webRtcLog("[onRemoveStream] #sfu; stream: $stream")
    }

    override fun onDataChannel(dataChannel: DataChannel?) {
        webRtcLog("[onDataChannel] ")
        dataChannel?.let {
            onDataChannelChanged(it)
        }
    }

    override fun onRenegotiationNeeded() {
        webRtcLog("[onRenegotiationNeeded] #sfu; no args")
    }

    override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        webRtcLog("[onStandartizedConnectionChanged] #sfu; no args")
    }
}