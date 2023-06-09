package ru.rikmasters.gilty.translation.viewer.viewmodel

import android.util.Log
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.Duration
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel
import ru.rikmasters.gilty.translation.shared.model.ConnectionStatus
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerEvent
import ru.rikmasters.gilty.translation.viewer.event.TranslationViewerOneTimeEvents
import ru.rikmasters.gilty.translation.viewer.model.TranslationViewerStatus
import ru.rikmasters.gilty.translation.viewer.model.TranslationViewerUiState
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.repository.TranslationRepository
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcAnswer
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcStatus
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationViewerViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
    private val meetingRepository: MeetingManager by inject()

    private var pingTimer: Timer? = Timer()
    private var durationTimer: Timer? = Timer()

    private val _translationViewerUiState = MutableStateFlow(TranslationViewerUiState())
    val translationViewerUiState = _translationViewerUiState.asStateFlow()

    private val _translationViewerOneTimeEvents = Channel<TranslationViewerOneTimeEvents>()
    val translationViewerOneTimeEvents = _translationViewerOneTimeEvents.receiveAsFlow()

    private val reloadChat = MutableStateFlow(false)
    private val reloadMembers = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()


    val messages = reloadChat.flatMapLatest {
        _translationViewerUiState.value.translationInfo?.id?.let {
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
        _translationViewerUiState.value.translationInfo?.id?.let {
            Log.d("TEST","id $it")
            translationRepository.getConnectedUsers(
                translationId = it,
                query = query
            )
        } ?: flow { }
    }.cachedIn(coroutineScope)

    init {
        coroutineScope.launch {
            translationRepository.webSocketFlow.collectLatest { socketEvent ->
                handleSocketEvents(socketEvent)
            }
        }
    }

    fun onEvent(event: TranslationViewerEvent) {
        when (event) {
            is TranslationViewerEvent.Initialize -> {
                event.meetingId.let { meetingId ->
                    loadTranslationInfo(meetingId)
                    loadMeetDetails(meetingId)
                    connectSocket(meetingId)
                    startPinging()
                    startDurationTimer()
                }
            }

            TranslationViewerEvent.Dismiss -> {
                disconnectSocket()
                stopPinging()
                stopDurationTimer()
                disconnectFromChat()
            }

            is TranslationViewerEvent.HandleWebRtcAnswer -> {
                handleWebRtcAnswer(event.answer)
            }

            is TranslationViewerEvent.HandleWebRtcStatus -> {
                handleWebRtcStatus(event.status)
            }

            is TranslationViewerEvent.MessageSent -> {
                sendMessage(event.messageText)
            }

            is TranslationViewerEvent.QueryChanged -> {
                changeQuery(event.newQuery)
            }

            TranslationViewerEvent.ConnectToChat -> {
                connectToChat()
            }

            TranslationViewerEvent.ReloadMembers -> {
                Log.d("TEST","Reload members")
                reloadMembers()
            }

            TranslationViewerEvent.DisconnectFromChat -> {
                disconnectFromChat()
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun connectToChat() {
        translationInfo { info ->
            coroutineScope.launch {
                translationRepository.connectToTranslationChat(
                    translationId = info.id
                )
            }
            reloadChat.value = !reloadChat.value
        }
    }

    private fun disconnectFromChat() {
        coroutineScope.launch {
            translationRepository.disconnectFromTranslationChat()
        }
        reloadChat.value = !reloadChat.value
    }

    private fun reloadMembers() {
        reloadMembers.value = !reloadMembers.value
    }

    private fun changeQuery(query: String) {
        _query.value = query
    }

    private fun sendMessage(messageText: String) {
        translationInfo { info ->
            coroutineScope.launch {
                translationRepository.sendMessage(
                    translationId = info.id,
                    text = messageText
                )
            }
        }
    }

    private fun handleWebRtcAnswer(answer: WebRtcAnswer) {
        when (answer) {
            WebRtcAnswer.weakConnection -> {
                coroutineScope.launch {
                    _translationViewerOneTimeEvents.send(
                        TranslationViewerOneTimeEvents.LowConnection
                    )
                }
            }
        }
    }

    private fun handleWebRtcStatus(status: WebRtcStatus) {
        _translationViewerUiState.value.translationStatus?.let { trStatus ->
            if (trStatus == TranslationViewerStatus.STREAM) {
                when (status) {
                    WebRtcStatus.reconnectAttemptsOver -> {
                        _translationViewerUiState.update {
                            it.copy(
                                connectionStatus = ConnectionStatus.NO_CONNECTION
                            )
                        }
                    }

                    WebRtcStatus.connecting -> {
                        _translationViewerUiState.update {
                            it.copy(
                                connectionStatus = ConnectionStatus.RECONNECTING
                            )
                        }
                    }

                    WebRtcStatus.connect, WebRtcStatus.stream -> {
                        _translationViewerUiState.update {
                            it.copy(
                                connectionStatus = ConnectionStatus.SUCCESS
                            )
                        }
                    }

                    WebRtcStatus.failed, WebRtcStatus.disconnect, WebRtcStatus.unknow -> {}
                }
            }
        }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     PINGING
     */
    private fun startPinging() {
        if (pingTimer == null) {
            pingTimer = Timer()
        }
        pingTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    translationInfo { info ->
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
      REMAIN TIME METHODS
     */
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
        translationInfo { info ->
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
            _translationViewerUiState.update {
                it.copy(
                    remainTime = diff
                )
            }
        }
    }

    private fun handleAppendTime(duration: Int? = null) {
        duration?.let {
            translationInfo { model ->
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
                        _translationViewerOneTimeEvents.send(
                            TranslationViewerOneTimeEvents.TranslationExtended(
                                duration = "$hourString$minuteString:00"
                            )
                        )
                    }
                }
            }
        } ?: kotlin.run {
            coroutineScope.launch {
                _translationViewerOneTimeEvents.send(
                    TranslationViewerOneTimeEvents.TranslationExtended(
                        duration = null
                    )
                )
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
      LOAD DETAILS METHODS
     */
    private fun loadTranslationInfo(
        meetingId: String
    ) {
        coroutineScope.launch {
            translationRepository.getTranslationInfo(
                translationId = meetingId
            ).on(
                loading = {},
                success = { translation ->
                    _translationViewerUiState.update {
                        it.copy(
                            translationInfo = translation,
                        )
                    }
                    if (translation.isStreaming) {
                        if (_translationViewerUiState.value.translationStatus == TranslationViewerStatus.PAUSED) {
                            _translationViewerOneTimeEvents.send(
                                TranslationViewerOneTimeEvents.TranslationResumed
                            )
                        }
                        _translationViewerUiState.update {
                            it.copy(
                                translationStatus = TranslationViewerStatus.STREAM,
                            )
                        }
                    }
                    _translationViewerOneTimeEvents.send(
                        TranslationViewerOneTimeEvents.ConnectToStream(
                            wsUrl = translation.webrtc
                        )
                    )
                },
                error = { cause ->
                    cause.serverMessage?.let {
                        _translationViewerOneTimeEvents.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _translationViewerOneTimeEvents.send(
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
                    _translationViewerUiState.update {
                        it.copy(
                            meetingModel = meetingModel
                        )
                    }
                },
                error = { cause ->
                    cause.serverMessage?.let {
                        _translationViewerOneTimeEvents.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _translationViewerOneTimeEvents.send(
                            TranslationViewerOneTimeEvents.OnError(
                                message = it
                            )
                        )
                    }
                }
            )
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun reconnect() {}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
       SOCKET HANDLING
     */
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

    private fun handleSocketEvents(socketEvent: TranslationCallbackEvents) {
        when (socketEvent) {
            TranslationCallbackEvents.MessageReceived -> {
                handleMessageReceived()
            }

            is TranslationCallbackEvents.SignalReceived -> {
                handleSignal(socketEvent.signal)
            }

            TranslationCallbackEvents.TranslationCompleted -> {
                handleBroadcastCompletion()
            }

            TranslationCallbackEvents.TranslationExpired -> {
                handleTranslationExpired()
            }

            is TranslationCallbackEvents.TranslationExtended -> {
                handleTranslationAppended(
                    socketEvent.completedAt, socketEvent.duration
                )
            }

            is TranslationCallbackEvents.UserConnected -> {
                handleUserConnected(socketEvent.user)
            }

            is TranslationCallbackEvents.UserDisconnected -> {
                handleUserDisconnected(socketEvent.user)
            }

            is TranslationCallbackEvents.UserKicked -> {
                handleUserKicked(socketEvent.user)
            }

            is TranslationCallbackEvents.MembersCountChanged -> {
                handleMembersCountChanged(socketEvent.count)
            }

            else -> {}
        }
    }

    private fun handleMessageReceived() {
        reloadChat.value = !reloadChat.value
    }

    private fun handleTranslationAppended(completedAt: LocalDateTime, duration: Int) {
        handleAppendTime(duration)
        _translationViewerUiState.update {
            it.copy(
                translationInfo = it.translationInfo?.copy(
                    status = TranslationStatusModel.ACTIVE,
                    completedAt = completedAt
                )
            )
        }
    }

    private fun handleMembersCountChanged(count: Int) {
        _translationViewerUiState.update {
            it.copy(
                membersCount = count
            )
        }
    }

    private fun handleUserKicked(user: UserModel) {
        translationInfo { translation ->
            if (user.id == translation.userId) {
                _translationViewerUiState.update {
                    it.copy(
                        translationStatus = TranslationViewerStatus.KICKED
                    )
                }
                coroutineScope.launch {
                    _translationViewerOneTimeEvents.send(TranslationViewerOneTimeEvents.DisconnectWebRtc)
                }
            } else {
                reloadMembers.value = !reloadMembers.value
            }
        }
    }

    private fun handleUserDisconnected(user: UserModel) {
        meetingInfo { meet ->
            if (user.id == meet.organizer.id) {
                _translationViewerUiState.update {
                    it.copy(
                        translationStatus = TranslationViewerStatus.PAUSED
                    )
                }
            }
            reloadMembers.value = !reloadMembers.value
        }
    }

    private fun handleUserConnected(user: UserModel) {
        meetingInfo { meet ->
            if (user.id == meet.organizer.id) {
                if (_translationViewerUiState.value.translationStatus == TranslationViewerStatus.PAUSED) {
                    coroutineScope.launch {
                        _translationViewerOneTimeEvents.send(
                            TranslationViewerOneTimeEvents.TranslationResumed
                        )
                    }
                }
                _translationViewerUiState.update {
                    it.copy(
                        translationStatus = TranslationViewerStatus.STREAM
                    )
                }
                reconnect()
            }
            reloadMembers.value = !reloadMembers.value
        }
    }

    private fun handleTranslationExpired() {
        _translationViewerUiState.update {
            it.copy(
                translationStatus = TranslationViewerStatus.PAUSED
            )
        }
    }

    private fun handleSignal(signal: TranslationSignalModel) {
        when (signal.signal) {
            TranslationSignalTypeModel.MICROPHONE -> {
                _translationViewerUiState.update {
                    it.copy(
                        translationInfo = it.translationInfo?.copy(
                            microphone = signal.value
                        )
                    )
                }
                if (!signal.value) {
                    coroutineScope.launch {
                        _translationViewerOneTimeEvents.send(
                            TranslationViewerOneTimeEvents.MicroOff
                        )
                    }
                }
            }

            TranslationSignalTypeModel.CAMERA -> {
                _translationViewerUiState.update {
                    it.copy(
                        translationInfo = it.translationInfo?.copy(
                            camera = signal.value
                        )
                    )
                }
            }
        }
    }

    private fun handleBroadcastCompletion() {
        disconnectSocket()
        _translationViewerUiState.update {
            it.copy(
                translationStatus = TranslationViewerStatus.COMPLETED
            )
        }
        coroutineScope.launch {
            _translationViewerOneTimeEvents.send(TranslationViewerOneTimeEvents.DisconnectWebRtc)
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
      UTILS
     */
    private fun translationInfo(
        block: (TranslationInfoModel) -> Unit
    ) {
        _translationViewerUiState.value.translationInfo?.let { info ->
            block(info)
        }
    }

    private fun meetingInfo(
        block: (FullMeetingModel) -> Unit
    ) {
        _translationViewerUiState.value.meetingModel?.let { info ->
            block(info)
        }
    }
}