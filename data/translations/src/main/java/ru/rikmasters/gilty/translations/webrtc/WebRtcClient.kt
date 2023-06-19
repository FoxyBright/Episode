package ru.rikmasters.gilty.translations.webrtc

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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
                        when (event) {
                            is WebRTCClientEvent.OfferSent -> {
                                // Отключаем таймер на ожидание офера
                                timer.cancel()
                                Log.d("TEST","TIMER CANCEL")
                                Log.d("TEST", "OFFER SENT")
                                val realOffer = event.offer.mapToRealOffer()
                                setIceServer(
                                    iceServers = realOffer.iceServers
                                )
                                val setSDPResult =
                                    peerConnection?.setRemoteDescription(realOffer.sdp)
                                setSDPResult?.onSuccess {
                                    realOffer.iceCandidates.forEach { candidate ->
                                        peerConnection?.addRtcIceCandidate(candidate)?.onFailure {
                                            _status.emit(WebRtcStatus.failed)
                                        }
                                    }
                                    answer()
                                }?.onFailure {
                                    _status.emit(WebRtcStatus.failed)
                                }
                            }
                            is WebRTCClientEvent.Connection -> {
                                Log.d("TEST","RECEIVED CONNECTION ${event.isConnected}")
                                if (!event.isConnected) {
                                    _status.emit(WebRtcStatus.failed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val _status = MutableSharedFlow<WebRtcStatus>(1, 0, BufferOverflow.DROP_OLDEST)
    val status = _status.onEach {
        Log.d("TEST","NEW STATUS $it")
        when (it) {
            WebRtcStatus.connect -> retry = 0
            WebRtcStatus.failed -> {
                destroy()
                _config?.let { config ->
                    Log.d("TEST","FAILED")
                    if (config.retryEnable && retry < config.retryCount) {
                        _status.emit(WebRtcStatus.connecting)
                        retry += 1
                        connecting(config)
                        Log.d("TEST","AFTER RECONNECT")
                    } else {
                        _status.emit(WebRtcStatus.reconnectAttemptsOver)
                    }
                }
            }

            WebRtcStatus.disconnect -> {
                destroy()
            }
            else -> {}
        }
    }.stateIn(sessionManagerScope, SharingStarted.Eagerly, WebRtcStatus.unknow)

    private var _config: WebRtcConfig? = null
    private var peerConnection: PeerConnection? = null
    var retry = 0
    private var channel: DataChannel? = null

    private val timer = object : CountDownTimer(
        _config?.retryInterval?.toLong() ?: 500L,
        _config?.retryInterval?.toLong() ?: 500L
    ) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            Log.d("TEST","TIMER FINISH")
            sessionManagerScope.launch {
                _status.emit(WebRtcStatus.failed)
            }
        }
    }

    private val signalingClient = SignalingClient()

    private var bitrateTimer: Timer? = Timer()

    private var audioLevelTimer: Timer? = Timer()

    private var previousStats: RTCStats? = null

    private val _remoteVideoSinkFlow = MutableSharedFlow<VideoTrack?>()
    val remoteVideoSinkFlow: SharedFlow<VideoTrack?> = _remoteVideoSinkFlow

    private val _webRtcAnswer = Channel<WebRtcAnswer>()
    val webRtcAnswer = _webRtcAnswer.receiveAsFlow()

    private val _audioLevel = MutableSharedFlow<Double?>(10, 0, BufferOverflow.DROP_OLDEST)
    val audioLevel = _audioLevel.asSharedFlow()

    fun connecting(config: WebRtcConfig) {
        _config = config
        webRtcLog("Connecting!!!")
        sessionManagerScope.launch {
            _status.emit(WebRtcStatus.connecting)
        }
        // Запускаем таймер на ожидание оффера
        timer.start()
        signalingClient.startConnection(config.wssUrl)
        doCollectSocket.value = true
    }

    fun disconnect() {
        sessionManagerScope.launch {
            _status.emit(WebRtcStatus.disconnect)
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
        sessionManagerScope.launch {
            _remoteVideoSinkFlow.emit(null)
        }
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
                    Log.d("TEST","ICE CHANGED $state")
                    when (state) {
                        RTCIceConnectionState.completed,
                        RTCIceConnectionState.connected -> {
                            sessionManagerScope.launch {
                                _status.emit(WebRtcStatus.connect)
                            }
                        }
                        RTCIceConnectionState.disconnected,
                        RTCIceConnectionState.failed -> {
                            sessionManagerScope.launch {
                                _status.emit(WebRtcStatus.failed)
                            }
                        }
                        else -> {}
                    }
                },
                onIceCandidateRemoved = {
                    sessionManagerScope.launch {
                        _status.emit(WebRtcStatus.failed)
                    }
                },
                onVideoTrack = { rtpTransceiver ->
                    rtpTransceiver.receiver?.track()?.let {
                        if (it.kind() == MediaStreamTrack.VIDEO_TRACK_KIND) {
                            val videoTrack = it as VideoTrack
                            sessionManagerScope.launch {
                                _remoteVideoSinkFlow.emit(videoTrack)
                                _status.emit(WebRtcStatus.stream)
                            }
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
                sessionManagerScope.launch {
                    _status.emit(WebRtcStatus.failed)
                }
            }
        }?.onFailure {
            sessionManagerScope.launch {
                _status.emit(WebRtcStatus.failed)
            }
        }
    }


}