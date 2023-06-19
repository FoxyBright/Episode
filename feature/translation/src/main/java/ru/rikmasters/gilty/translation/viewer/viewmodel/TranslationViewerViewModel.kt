package ru.rikmasters.gilty.translation.viewer.viewmodel

import android.util.Log
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translation.shared.utils.getAdditionalTimeString
import ru.rikmasters.gilty.translation.shared.utils.getTimeDifferenceString
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerEvent
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerOneTimeEvents
import ru.rikmasters.gilty.translation.viewer.model.ViewerCustomHUD
import ru.rikmasters.gilty.translation.viewer.model.ViewerHUD
import ru.rikmasters.gilty.translation.viewer.model.ViewerSnackbarState
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.repository.TranslationRepository
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcAnswer
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcStatus
import java.time.ZonedDateTime
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationViewerViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
    private val meetingRepository: MeetingManager by inject()

    private val _meeting = MutableStateFlow<FullMeetingModel?>(null)
    val meeting = _meeting.asStateFlow()

    private val _translation = MutableStateFlow<TranslationInfoModel?>(null)
    val translation = _translation.onEach {
        it?.let { translation ->
            startDurationTimer()
            _cameraState.value = translation.camera
            _microphoneState.value = translation.microphone
            _isStreaming.value = translation.isStreaming
            _completedAt.value = translation.completedAt
            _oneTimeEvent.send(TranslationViewerOneTimeEvents.ConnectToStream(translation.webrtc))
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)

    private val _completedAt = MutableStateFlow<ZonedDateTime?>(null)

    private val _cameraState = MutableStateFlow(true)
    val cameraState = _cameraState.asStateFlow()

    private val _microphoneState = MutableStateFlow(true)
    val microphoneState = _microphoneState.onEach {
        if (!it) {
            coroutineScope.launch {
                _viewerSnackbarState.emit(ViewerSnackbarState.MICRO_OFF)
            }
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), true)

    private val _isStreaming = MutableStateFlow(true)
    val isStreaming = _isStreaming.onEach {
        if (!it) {
            _hudState.value = ViewerHUD.PAUSED
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), true)

    private val _remainTime = MutableStateFlow("")
    val remainTime = _remainTime.asStateFlow()

    private val _translationStatus = MutableStateFlow<TranslationStatusModel?>(null)
    val translationStatus = _translationStatus.asStateFlow()

    private val _hudState = MutableStateFlow<ViewerHUD?>(null)
    val hudState = _hudState.asStateFlow()

    private val _membersCount = MutableStateFlow(0)
    val membersCount = _membersCount.asStateFlow()

    private val _viewerSnackbarState =
        MutableSharedFlow<ViewerSnackbarState?>(1, 0, BufferOverflow.DROP_OLDEST)
    val viewerSnackbarState = _viewerSnackbarState
        .onEach {
            _oneTimeEvent.send(TranslationViewerOneTimeEvents.ShowSnackbar)
        }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    private val _oneTimeEvent = Channel<TranslationViewerOneTimeEvents>()
    val translationViewerOneTimeEvents = _oneTimeEvent.receiveAsFlow()

    private val _customHUDState = MutableStateFlow<ViewerCustomHUD?>(null)
    val customHUDState = _customHUDState.asStateFlow()

    private val _additionalTime = MutableStateFlow("")
    val additionalTime = _additionalTime.asStateFlow()

    private val _timerHighlighted = MutableStateFlow(false)
    val timerHighlighted = _timerHighlighted.asStateFlow()

    val placeHolderVisible = combine(_hudState, _customHUDState) { hud, state ->
        Pair(hud, state)
    }.map { (hud, state) ->
        when (state) {
            null -> {
                hud == null
            }
            else -> false
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)

    private var pingTimer: Timer? = Timer()
    private var durationTimer: Timer? = Timer()

    private val reloadChat = MutableStateFlow(false)
    private val reloadMembers = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val messages = reloadChat.flatMapLatest {
        _translation.value?.id?.let {
            translationRepository.getMessages(
                translationId = it
            )
        } ?: flow { }
    }.cachedIn(coroutineScope)

    val members = combine(
        reloadMembers,
        _query
    ) { reload, query ->
        Pair(reload, query)
    }.flatMapLatest { (_, query) ->
        _translation.value?.id?.let {
            translationRepository.getConnectedUsers(
                translationId = it,
                query = query
            )
        } ?: flow { }
    }.cachedIn(coroutineScope)

    fun onEvent(event: TranslationViewerEvent) {
        when (event) {
            is TranslationViewerEvent.Initialize -> {
                event.meetingId.let { meetingId ->
                    loadTranslationInfo(meetingId)
                    loadMeetDetails(meetingId)
                    connectSocket()
                    startPinging()
                    startDurationTimer()
                    connectToTranslationChat()
                }
            }

            TranslationViewerEvent.Dismiss -> {
                disconnectSocket()
                stopPinging()
                stopDurationTimer()
                //disconnectFromTranslationChat()
                coroutineScope.launch {
                    _oneTimeEvent.send(TranslationViewerOneTimeEvents.DisconnectWebRtc)
                }
            }

            TranslationViewerEvent.EnterBackground -> {
                disconnectSocket()
                stopPinging()
                disconnectFromTranslationChat()
                coroutineScope.launch {
                    _oneTimeEvent.send(TranslationViewerOneTimeEvents.DisconnectWebRtc)
                }
            }

            is TranslationViewerEvent.EnterForeground -> {
                event.meetingId.let { meetingId ->
                    loadTranslationInfo(meetingId)
                    loadMeetDetails(meetingId)
                    connectToTranslationChat()
                    connectSocket()
                    startPinging()
                }
            }

            is TranslationViewerEvent.HandleWebRtcAnswer -> {
                when (event.answer) {
                    WebRtcAnswer.weakConnection -> {
                        coroutineScope.launch {
                            _viewerSnackbarState.emit(ViewerSnackbarState.WEAK_CONNECTION)
                        }
                    }
                }
            }

            is TranslationViewerEvent.HandleWebRtcStatus -> {
                if (_isStreaming.value) {
                    when (event.status) {
                        WebRtcStatus.connecting -> {
                            _hudState.value = ViewerHUD.RECONNECTING
                        }

                        WebRtcStatus.connect, WebRtcStatus.stream -> {
                            _hudState.value = null
                        }

                        WebRtcStatus.reconnectAttemptsOver -> {
                            _hudState.value = ViewerHUD.RECONNECT_FAILED
                        }

                        WebRtcStatus.unknow, WebRtcStatus.disconnect, WebRtcStatus.failed -> {}
                    }
                }
            }

            is TranslationViewerEvent.MessageSent -> {
                _translation.value?.let { translation ->
                    coroutineScope.launch {
                        translationRepository.sendMessage(
                            translationId = translation.id,
                            text = event.messageText
                        )
                    }
                }
            }

            is TranslationViewerEvent.QueryChanged -> {
                _query.value = event.newQuery
            }

            TranslationViewerEvent.ConnectToChat -> {
                reloadChat.value = !reloadChat.value
            }

            TranslationViewerEvent.ReloadMembers -> {
                reloadMembers.value = !reloadMembers.value
            }

            TranslationViewerEvent.DisconnectFromChat -> {
                reloadChat.value = !reloadChat.value
            }

            TranslationViewerEvent.Reconnect -> {
                reconnect()
            }
        }
    }

    init {
        coroutineScope.launch {
            translationRepository.webSocketFlow.collectLatest { socketEvent ->
                Log.d("TEST", "NEW WEb SOCKET EVENT $socketEvent")
                when (socketEvent) {
                    TranslationCallbackEvents.MessageReceived -> {
                        reloadChat.value = !reloadChat.value
                    }

                    is TranslationCallbackEvents.SignalReceived -> {
                        when (socketEvent.signal.signal) {
                            TranslationSignalTypeModel.MICROPHONE -> {
                                _microphoneState.value = socketEvent.signal.value
                            }

                            TranslationSignalTypeModel.CAMERA -> {
                                _cameraState.value = socketEvent.signal.value
                            }
                        }
                    }

                    TranslationCallbackEvents.TranslationCompleted -> {
                        disconnectSocket()
                        coroutineScope.launch {
                            _oneTimeEvent.send(TranslationViewerOneTimeEvents.DisconnectWebRtc)
                        }
                        _customHUDState.value = ViewerCustomHUD.COMPLETED
                    }

                    TranslationCallbackEvents.TranslationExpired -> {
                        _hudState.value = ViewerHUD.PAUSED
                    }

                    is TranslationCallbackEvents.TranslationExtended -> {
                        _hudState.value = null
                        if (ZonedDateTime.now().isBefore(_completedAt.value)) {
                            coroutineScope.launch {
                                _additionalTime.value =
                                    getAdditionalTimeString(socketEvent.duration)
                                delay(2000)
                                _additionalTime.value = ""
                                _completedAt.value = socketEvent.completedAt
                            }
                        } else {
                            coroutineScope.launch {
                                _customHUDState.value = null
                                _completedAt.value = socketEvent.completedAt
                                _timerHighlighted.value = true
                                delay(2000)
                                _timerHighlighted.value = false
                            }
                        }
                    }

                    is TranslationCallbackEvents.UserConnected -> {
                        _meeting.value?.let {
                            if (socketEvent.user.id == it.organizer.id) {
                                _isStreaming.value = true
                                reconnect()
                            }
                        }
                        reloadMembers.value = !reloadMembers.value
                    }

                    is TranslationCallbackEvents.UserDisconnected -> {
                        _meeting.value?.let {
                            if (socketEvent.user.id == it.organizer.id) {
                                _isStreaming.value = false
                            }
                        }
                        reloadMembers.value = !reloadMembers.value
                    }

                    is TranslationCallbackEvents.UserKicked -> {
                        _translation.value?.let {
                            if (socketEvent.user.id == it.userId) {
                                disconnectSocket()
                                coroutineScope.launch {
                                    _oneTimeEvent.send(TranslationViewerOneTimeEvents.DisconnectWebRtc)
                                }
                                _customHUDState.value = ViewerCustomHUD.KICKED
                            }
                        }
                        reloadMembers.value = !reloadMembers.value
                    }

                    is TranslationCallbackEvents.MembersCountChanged -> {
                        _membersCount.value = socketEvent.count
                    }

                    else -> {}
                }
            }
        }
    }

    private fun reconnect() {
        _translation.value?.let {
            loadTranslationInfo(it.id)
        }
        coroutineScope.launch {
            _translation.value?.webrtc?.let {
                _oneTimeEvent.send(TranslationViewerOneTimeEvents.ConnectToStream(it))
            }
        }
    }

    private fun startPinging() {
        if (pingTimer == null) {
            pingTimer = Timer()
        }
        pingTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    _translation.value?.let { info ->
                        coroutineScope.launch {
                            translationRepository.ping(
                                translationId = info.id
                            )
                        }
                    }
                }
            }, 0, 8000
        )
    }

    private fun stopPinging() {
        pingTimer?.cancel()
        pingTimer = null
    }

    private fun startDurationTimer() {
        if (durationTimer == null) {
            durationTimer = Timer()
        }
        durationTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    _completedAt.value?.let { time ->
                        _remainTime.value = getTimeDifferenceString(time)
                    }
                }
            }, 0, 1000
        )
    }

    private fun stopDurationTimer() {
        durationTimer?.cancel()
        durationTimer = null
    }

    private fun loadTranslationInfo(
        meetingId: String
    ) {
        coroutineScope.launch {
            translationRepository.getTranslationInfo(
                translationId = meetingId
            ).on(
                loading = {},
                success = { translation -> _translation.value = translation },
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    }
                }
            )
        }
    }

    private fun loadMeetDetails(
        meetingId: String
    ) {
        coroutineScope.launch {
            meetingRepository.getDetailedMeet(
                meetId = meetingId
            ).on(
                loading = {},
                success = { meetingModel ->
                    _meeting.value = meetingModel
                    connectToTranslationChat()
                },
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    }
                }
            )
        }
    }

    private fun connectSocket() {
        translation.value?.let {
            coroutineScope.launch {
                translationRepository.connectToTranslation(
                    translationId = it.id
                )
            }
        }
    }

    private fun disconnectSocket() {
        coroutineScope.launch {
            translationRepository.disconnectFromTranslation()
        }
    }

    private fun connectToTranslationChat() {
        _translation.value?.let {
            coroutineScope.launch {
                translationRepository.connectToTranslationChat(
                    translationId = it.id
                )
            }
        }
    }

    private fun disconnectFromTranslationChat() {
        coroutineScope.launch {
            translationRepository.disconnectFromTranslationChat()
        }
    }
}