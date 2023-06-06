package ru.rikmasters.gilty.translations.webrtc

import android.content.Context
import android.os.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.RTCStats
import org.webrtc.RTCStatsReport
import org.webrtc.VideoTrack
import ru.rikmasters.gilty.translations.webrtc.model.RTCIceConnectionState
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcAnswer
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcConfig
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcStatus
import ru.rikmasters.gilty.translations.webrtc.model.signalling.mapToRealOffer
import ru.rikmasters.gilty.translations.webrtc.utils.addRtcIceCandidate
import ru.rikmasters.gilty.translations.webrtc.utils.createAnswer
import ru.rikmasters.gilty.translations.webrtc.utils.createPeerConnectionFactory
import ru.rikmasters.gilty.translations.webrtc.utils.setLocalDescription
import ru.rikmasters.gilty.translations.webrtc.utils.setRemoteDescription
import ru.rikmasters.gilty.translations.webrtc.utils.webRtcLog
import java.util.Timer
import java.util.TimerTask

class WebRtcClient(
    private val context: Context
) {

    // Коллект сокетов только когда клиент активен
    private val doCollectSocket = MutableStateFlow(false)
    private val sessionManagerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val eglBaseContext: EglBase.Context = EglBase.create().eglBaseContext
    private val connectionFactory = createPeerConnectionFactory(context, eglBaseContext).first
    init {
        sessionManagerScope.launch {
            doCollectSocket.collectLatest { doCollect ->
                if (doCollect) {
                    signalingClient.signalingEventFlow.collectLatest { event ->
                        // Отключаем таймер на ожидание офера
                        timer.cancel()
                        when (event) {
                            is WebRTCClientEvent.OfferSent -> {
                                val realOffer = event.offer.mapToRealOffer()
                                setIceServer(
                                    iceServers = realOffer.iceServers
                                )
                                val setSDPResult = peerConnection?.setRemoteDescription(realOffer.sdp)
                                setSDPResult?.onSuccess {
                                    realOffer.iceCandidates.forEach { candidate ->
                                        peerConnection?.addRtcIceCandidate(candidate)?.onFailure {
                                            status = WebRtcStatus.failed
                                        }
                                    }
                                    answer()
                                }?.onFailure {
                                    status = WebRtcStatus.failed
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
    private var status: WebRtcStatus = WebRtcStatus.unknow
        set(value) {
            setStatus()
            if (field != value) field = value
            sessionManagerScope.launch {
                _webRtcStatus.send(status)
            }

        }
    private var _config: WebRtcConfig? = null
    private var peerConnection: PeerConnection? = null
    private val timer = object : CountDownTimer(_config?.retryInterval?.toLong() ?: 500L, _config?.retryInterval?.toLong() ?: 500L) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            status = WebRtcStatus.failed
        }
    }
    var retry = 0
    private var channel: DataChannel? = null

    private val signalingClient = SignalingClient()

    private var bitrateTimer: Timer? = Timer()

    private var audioLevelTimer: Timer? = Timer()

    private var previousStats: RTCStats? = null

    private val _remoteVideoSinkFlow = MutableSharedFlow<VideoTrack>()
    val remoteVideoSinkFlow: SharedFlow<VideoTrack> = _remoteVideoSinkFlow


    private val _webRtcStatus = Channel<WebRtcStatus>()
    val webRtcStatus = _webRtcStatus.receiveAsFlow()

    private val _webRtcAnswer = Channel<WebRtcAnswer>()
    val webRtcAnswer = _webRtcAnswer.receiveAsFlow()

    private val _audioLevel = MutableSharedFlow<Double?>(10, 0, BufferOverflow.DROP_OLDEST)
    val audioLevel = _audioLevel.asSharedFlow()

    fun connecting(config: WebRtcConfig) {
        _config = config
        webRtcLog("Connecting!!!")
        status = WebRtcStatus.connecting
        // Запускаем таймер на ожидание оффера
        timer.start()
        signalingClient.startConnection(config.wssUrl)
        doCollectSocket.value = !doCollectSocket.value
    }

    fun disconnect() {
        status = WebRtcStatus.disconnect
    }

    private fun setStatus() {
        when (status) {
            WebRtcStatus.connect -> retry = 0
            WebRtcStatus.failed -> {
                destroy()
                _config?.let { config ->
                    if (config.retryEnable && retry < config.retryCount) {
                        status = WebRtcStatus.connecting
                        retry += 1
                        connecting(config)
                    }
                }
            }
            WebRtcStatus.disconnect -> {
                destroy()
            }
            else -> {}
        }
    }

    private fun destroy() {
        signalingClient.destroy()
        peerConnection?.close()
        peerConnection = null

        bitrateTimer?.cancel()
        bitrateTimer = null
        audioLevelTimer?.cancel()
        audioLevelTimer = null

    }

    private fun setIceServer(iceServers: List<IceServer>) {
        val config = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }
        val mediaConstraints = MediaConstraints()
        peerConnection = connectionFactory.createPeerConnection(
            config, mediaConstraints, PeerConnectionObserver(
                onDataChannelChanged = {
                    channel = it
                },
                onIceConnectionStateChanged = { state ->
                    when (state) {
                        RTCIceConnectionState.completed,
                        RTCIceConnectionState.connected -> {
                            status = WebRtcStatus.connect
                        }

                        RTCIceConnectionState.disconnected,
                        RTCIceConnectionState.failed -> {
                            status = WebRtcStatus.failed
                        }
                        else -> {}
                    }
                },
                onIceCandidateRemoved = {
                    status = WebRtcStatus.failed
                },
                onVideoTrack = { rtpTransceiver ->
                    rtpTransceiver.receiver?.track()?.let {
                        if (it.kind() == MediaStreamTrack.VIDEO_TRACK_KIND) {
                            val videoTrack = it as VideoTrack
                            sessionManagerScope.launch {
                                _remoteVideoSinkFlow.emit(videoTrack)
                            }
                            status = WebRtcStatus.stream
                        }
                    }
                }
            )
        )
        previousStats = null
        setTimers()
    }

    private fun setTimers() {
        audioLevelTimer?.cancel()
        audioLevelTimer = null
        bitrateTimer?.cancel()
        bitrateTimer = null
        if (audioLevelTimer == null) {
            audioLevelTimer = Timer()
        }
        if (bitrateTimer == null) {
            bitrateTimer = Timer()
        }
        audioLevelTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    peerConnection?.getStats { report ->
                        audioLevel(report)
                    }
                }
            }, 0, 10
        )
        bitrateTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    peerConnection?.getStats { report ->
                        processLowInternetConnection(report)
                    }
                }
            }, 0, 1000
        )
    }

    private fun processLowInternetConnection(report: RTCStatsReport) {
        report.statsMap.forEach { statMap ->
            if (statMap.value.type == "inbound-rtp" && (statMap.value.members["mediaType"] as? String) == "video") {
                webRtcLog("[LOW CONNECTION] ${statMap.value}")
                previousStats?.let { prevStat ->
                    val seconds = (statMap.value.timestampUs - prevStat.timestampUs) / 1_000_000
                    val currentBytes = (statMap.value.members["bytesReceived"] as? Int) ?: 0
                    val previousBytes = (prevStat.members["bytesReceived"] as? Int) ?: 0
                    if (seconds > 0) {
                        val bytesPerSecond = (currentBytes - previousBytes) / seconds.toInt()
                        val kbPerSecond = bytesPerSecond / 1024
                        if (kbPerSecond < 70) {
                            sessionManagerScope.launch {
                                _webRtcAnswer.send(WebRtcAnswer.weakConnection)
                            }
                        }
                    }
                }
            }
        }
    }

    private val AUDIO_LEVEL_STATS_KEY = "audioLevel"
    private fun audioLevel(report: RTCStatsReport) {
        findAudioStats(report.statsMap)?.let {
            (it.members[AUDIO_LEVEL_STATS_KEY] as? Double)?.let { audioLevel ->
                sessionManagerScope.launch {
                    _audioLevel.emit(
                        audioLevel
                    )
                }
            }
        }
    }

    private fun findAudioStats(stats: Map<String, RTCStats>): RTCStats? {
        stats.forEach { mapEntry ->
            mapEntry.value.members[AUDIO_LEVEL_STATS_KEY]?.let {
                return mapEntry.value
            } ?: kotlin.run {
            }
        }
        return null
    }

    private suspend fun answer() {
        val constraints = MediaConstraints()
        val sdpResult = peerConnection?.createAnswer(constraints)
        sdpResult?.onSuccess {
            peerConnection?.setLocalDescription(it)?.onSuccess { sdp ->
                signalingClient.answer(sdp)
            }?.onFailure {
                status = WebRtcStatus.failed
            }
        }?.onFailure {
            status = WebRtcStatus.failed
        }
    }


}