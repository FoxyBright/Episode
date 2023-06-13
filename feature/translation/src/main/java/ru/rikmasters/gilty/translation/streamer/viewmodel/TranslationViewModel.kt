package ru.rikmasters.gilty.translation.streamer.viewmodel

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.Duration
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel.CAMERA
import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel.MICROPHONE
import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel
import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel.COMPLETED
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translation.streamer.event.TranslationEvent
import ru.rikmasters.gilty.translation.streamer.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.streamer.model.RTMPStatus
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing.BACK
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing.FRONT
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD.RECONNECTING
import ru.rikmasters.gilty.translation.streamer.model.StreamerHUD.RECONNECT_FAILED
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState.BROADCAST_EXTENDED
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState.MICRO_OFF
import ru.rikmasters.gilty.translation.streamer.model.StreamerSnackbarState.WEAK_CONNECTION
import ru.rikmasters.gilty.translation.streamer.model.StreamerViewState
import ru.rikmasters.gilty.translation.streamer.model.StreamerViewState.PREVIEW
import ru.rikmasters.gilty.translation.streamer.model.StreamerViewState.STREAM
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.repository.TranslationRepository
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
    private val meetingRepository: MeetingManager by inject()

    private var pingTimer: Timer? = Timer()
    private var durationTimer: Timer? = Timer()

    private val _membersCount = MutableStateFlow(0)
    val membersCount = _membersCount.asStateFlow()

    private val retryCount = MutableStateFlow(8)

    private val _additionalTime = MutableStateFlow("")
    val additionalTime = _additionalTime.asStateFlow()

    private val _remainTime = MutableStateFlow("")
    val remainTime = _remainTime.asStateFlow()

    private val _translation = MutableStateFlow<TranslationInfoModel?>(null)
    val translation = _translation.onEach {
        it?.let { translation ->
            startDurationTimer()
            _oneTimeEvent.send(TranslationOneTimeEvent.StartStream(it.rtmp ?: ""))
            _translationStatus.value = translation.status
            _microphone.value = translation.microphone
            _camera.value = translation.camera
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)

    private val _meeting = MutableStateFlow<FullMeetingModel?>(null)
    val meeting = _meeting.asStateFlow()

    private val _translationStatus = MutableStateFlow<TranslationStatusModel?>(null)
    val translationStatus = _translationStatus.onEach {
        it?.let { translationStatus ->
            when (translationStatus) {
                TranslationStatusModel.ACTIVE -> {
                    _hudState.value = null
                }

                TranslationStatusModel.EXPIRED -> {
                    _streamerViewState.value = StreamerViewState.EXPIRED
                }

                COMPLETED -> {
                    _streamerViewState.value = StreamerViewState.COMPLETED
                }

                else -> {}
            }
        }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    private val _microphone = MutableStateFlow(true)
    val microphone = _microphone.onEach { value ->
        _oneTimeEvent.send(TranslationOneTimeEvent.ToggleMicrophone(value))
        _translation.value?.id?.let {
            translationRepository.sendSignal(it, MICROPHONE, value)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), true)

    private val _camera = MutableStateFlow(true)
    val camera = _camera.onEach { value ->
        _oneTimeEvent.send(TranslationOneTimeEvent.ToggleCamera(value))
        _translation.value?.id?.let {
            translationRepository.sendSignal(it, CAMERA, value)
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), true)

    private val _facing = MutableStateFlow(FRONT)
    val facing = _facing.onEach { value ->
        _oneTimeEvent.send(TranslationOneTimeEvent.ChangeFacing(value))
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), FRONT)

    private val _hudState = MutableStateFlow<StreamerHUD?>(null)
    val hudState = _hudState.asStateFlow()

    private val _streamerViewState = MutableStateFlow(PREVIEW)
    val streamerViewState = _streamerViewState.asStateFlow()

    val placeHolderVisible = combine(_hudState, _streamerViewState) { hud, state ->
        Pair(hud, state)
    }.map { (hud, state) ->
        when (state) {
            STREAM -> {
                hud == null
            }

            else -> false
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)

    private val _streamerSnackbarState = MutableSharedFlow<StreamerSnackbarState?>(1, 0, BufferOverflow.DROP_OLDEST)
    val streamerSnackbarState = _streamerSnackbarState
        .onEach {
            _oneTimeEvent.send(TranslationOneTimeEvent.ShowSnackbar)
        }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    private val _oneTimeEvent = Channel<TranslationOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

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

    init {
        coroutineScope.launch {
            translationRepository.webSocketFlow.collectLatest { socketEvent ->
                when (socketEvent) {
                    TranslationCallbackEvents.MessageReceived -> {
                        reloadChat.value = !reloadChat.value
                    }

                    TranslationCallbackEvents.TranslationCompleted -> {
                        _translationStatus.value = COMPLETED
                    }

                    TranslationCallbackEvents.TranslationExpired -> {
                        _translationStatus.value = TranslationStatusModel.EXPIRED
                    }

                    is TranslationCallbackEvents.UserConnected -> {
                        reloadMembers.value = !reloadMembers.value
                    }

                    is TranslationCallbackEvents.SignalReceived -> {
                        if (socketEvent.signal.signal == MICROPHONE) {
                            if (!socketEvent.signal.value) {
                                coroutineScope.launch {
                                    _streamerSnackbarState.emit(MICRO_OFF)
                                }
                            }
                        }
                    }

                    is TranslationCallbackEvents.UserDisconnected -> {
                        reloadMembers.value = !reloadMembers.value
                    }

                    is TranslationCallbackEvents.UserKicked -> {
                        reloadMembers.value = !reloadMembers.value
                    }

                    is TranslationCallbackEvents.TranslationExtended -> {
                        handleTranslationAppended(
                            socketEvent.completedAt, socketEvent.duration
                        )
                    }

                    is TranslationCallbackEvents.MembersCountChanged -> {
                        _membersCount.value = socketEvent.count
                    }

                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: TranslationEvent) {
        when (event) {
            is TranslationEvent.Initialize -> {
                event.meetingId.let { meetingId ->
                    loadTranslationInfo(meetingId)
                    loadMeetDetails(meetingId)
                }
            }

            TranslationEvent.Dismiss -> {
                disconnectSocket()
                stopPinging()
                stopDurationTimer()
            }

            TranslationEvent.EnterBackground -> {
                //TODO: Destroy RTMP
                disconnectSocket()
                stopPinging()
            }

            is TranslationEvent.EnterForeground -> {
                loadTranslationInfo(event.meetingId)
                connectSocket(event.meetingId)
                startPinging()
            }

            TranslationEvent.ChangeFacing -> {
                _facing.value = if (_facing.value == FRONT) BACK else FRONT
            }

            is TranslationEvent.AppendTranslation -> {
                _translation.value?.let { info ->
                    extendTranslation(
                        translationId = info.id,
                        appendMinutes = (event.appendMinutes).toLong()
                    )
                }
            }

            TranslationEvent.CompleteTranslation -> {
                _translationStatus.value = COMPLETED
                _translation.value?.let { info ->
                    coroutineScope.launch {
                        translationRepository.endTranslation(
                            translationId = info.id
                        )
                    }
                }
                coroutineScope.launch {
                    _oneTimeEvent.send(
                        TranslationOneTimeEvent.CompleteTranslation
                    )
                }
            }

            TranslationEvent.ChangeUiToPreview -> {
                _streamerViewState.value = PREVIEW
            }

            TranslationEvent.ChangeUiToStream -> {
                _streamerViewState.value = STREAM
            }

            is TranslationEvent.ChatBottomSheetOpened -> {
                /*
                if (event.isOpened) {
                    translationInfo { info ->
                        coroutineScope.launch {
                            translationRepository.connectToTranslationChat(
                                translationId = info.id
                            )
                        }
                        reloadChat.value = !reloadChat.value
                    }
                } else {
                    coroutineScope.launch {
                        translationRepository.disconnectFromTranslationChat()
                    }
                    reloadChat.value = !reloadChat.value
                }

                 */
            }

            is TranslationEvent.KickUser -> {
                _translation.value?.let { translation ->
                    coroutineScope.launch {
                        event.user.id?.let { userId ->
                            translationRepository.kickUser(
                                translationId = translation.id,
                                userId = userId
                            )
                        }
                    }
                }
            }

            TranslationEvent.Reconnect -> {
                retryCount.value = 8
                _translation.value?.id?.let {
                    loadTranslationInfo(it)
                }
                _hudState.value = RECONNECTING
            }

            is TranslationEvent.SendMessage -> {
                _translation.value?.let { translation ->
                    coroutineScope.launch {
                        translationRepository.sendMessage(
                            translationId = translation.id,
                            text = event.text
                        )
                    }
                }
            }

            is TranslationEvent.UserBottomSheetOpened -> {
                if (event.isOpened) {
                    reloadMembers.value = !reloadMembers.value
                }
            }

            is TranslationEvent.UserBottomSheetQueryChanged -> {
                _query.value = event.newQuery
            }

            TranslationEvent.ToggleCamera -> {
                _camera.value = !_camera.value
            }

            TranslationEvent.ToggleMicrophone -> {
                _microphone.value = !_microphone.value
            }

            is TranslationEvent.ProcessRTMPStatus -> {
                when (event.status) {
                    RTMPStatus.CONNECTED -> {
                        _hudState.value = null
                    }

                    RTMPStatus.FAILED -> {
                        if (retryCount.value > 0) {
                            _hudState.value = RECONNECTING
                        } else {
                            _hudState.value = RECONNECT_FAILED
                        }
                        retryCount.value = retryCount.value - 1
                    }
                }
            }

            TranslationEvent.LowBitrate -> {
                coroutineScope.launch {
                    _streamerSnackbarState.emit(WEAK_CONNECTION)
                }
            }
        }
    }

    private fun extendTranslation(translationId: String, appendMinutes: Long) {
        coroutineScope.launch {
            translationRepository.extendTranslation(
                translationId = translationId,
                duration = appendMinutes
            ).on(
                loading = {},
                success = {},
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.OnError(
                                message = it
                            )
                        )
                    }
                }
            )
        }
    }

    private fun startDurationTimer() {
        if (durationTimer == null) {
            durationTimer = Timer()
        }
        durationTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    calculateTimeDiff()
                }
            }, 0, 1000
        )
    }

    private fun stopDurationTimer() {
        durationTimer?.cancel()
        durationTimer = null
    }

    private fun calculateTimeDiff() {
        _translation.value?.let { info ->
            val current = LocalDateTime.nowZ()
            val duration = Duration.between(
                current, info.completedAt
            )
            val hours = (duration / 3600000)
            val minutes = (duration - (hours * 3600000)) / 60000
            val seconds = (duration - (hours * 3600000) - (minutes * 60000)) / 1000
            val hourString = if (hours > 0) "$hours:" else ""
            val minuteString = if (hours > 0) {
                if (minutes < 10) {
                    "0$minutes"
                } else {
                    "$minutes"
                }
            } else {
                "$minutes"
            }
            val secondString = if (minutes > 0) {
                if (seconds < 10) {
                    "0$seconds"
                } else {
                    "$seconds"
                }
            } else {
                "$seconds"
            }
            val diff = "$hourString$minuteString:$secondString"
            _remainTime.value = diff
        }
    }

    private fun handleAppendTime(duration: Int? = null) {
        duration?.let {
            _translation.value?.let { model ->
                if (LocalDateTime.nowZ().isBefore(model.completedAt)) {
                    val hour = (duration / 60).takeIf { it > 0 }
                    val hourString = hour?.let { "$hour:" } ?: ""
                    val minute = hour?.let { duration - 60 } ?: duration
                    val minuteString = hour?.let {
                        if (minute < 10) {
                            "0$minute"
                        } else {
                            "$minute"
                        }
                    } ?: "$minute"
                    coroutineScope.launch {
                        _streamerSnackbarState.emit(BROADCAST_EXTENDED)
                    }
                    _additionalTime.value = "$hourString$minuteString:00"
                }
            }
        } ?: kotlin.run {
            coroutineScope.launch {
                _streamerSnackbarState.emit(BROADCAST_EXTENDED)
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


    private fun connectSocket(meetingId: String) {
        coroutineScope.launch {
            translationRepository.connectToTranslation(
                translationId = meetingId
            )
        }
    }

    private fun disconnectSocket() {
        coroutineScope.launch {
            translationRepository.disconnectFromTranslation()
        }
    }

    private fun handleTranslationAppended(completedAt: LocalDateTime, duration: Int) {
        coroutineScope.launch {
            handleAppendTime(duration)
            delay(2000)
            _translation.update {
                it?.copy(
                    status = TranslationStatusModel.ACTIVE,
                    completedAt = completedAt
                )
            }
        }
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
                            TranslationOneTimeEvent.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.OnError(
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
                success = { meetingModel -> _meeting.value = meetingModel },
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.OnError(
                                message = it
                            )
                        )
                    }
                }
            )
        }
    }
}