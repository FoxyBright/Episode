package ru.rikmasters.gilty.translation.streamer.viewmodel

import android.util.Log
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel
import ru.rikmasters.gilty.translation.shared.model.ConnectionStatus
import ru.rikmasters.gilty.translation.streamer.event.TranslationEvent
import ru.rikmasters.gilty.translation.streamer.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.streamer.model.Facing
import ru.rikmasters.gilty.translation.streamer.model.TranslationStreamerStatus
import ru.rikmasters.gilty.translation.streamer.model.TranslationStreamerUiState
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

    private val _translationStreamerUiState = MutableStateFlow(TranslationStreamerUiState())
    val translationStreamerUiState = _translationStreamerUiState.asStateFlow()
    private val _oneTimeEvent = Channel<TranslationOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    private val reloadChat = MutableStateFlow(false)
    private val reloadMembers = MutableStateFlow(false)
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val messages = reloadChat.flatMapLatest {
        _translationStreamerUiState.value.translationInfo?.id?.let {
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
        _translationStreamerUiState.value.translationInfo?.id?.let {
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

            TranslationEvent.ChangeFacing -> {
                changeFacing()
            }

            is TranslationEvent.AppendTranslation -> {
                translationInfo { info ->
                    extendTranslation(
                        translationId = info.id,
                        appendMinutes = (event.appendMinutes).toLong()
                    )
                }
            }

            TranslationEvent.CompleteTranslation -> {
                completeTranslation()
            }

            TranslationEvent.ChangeUiToPreview -> {
                changeUiToPreview()
            }

            TranslationEvent.ChangeUiToStream -> {
                changeUiToStream()
            }

            is TranslationEvent.ChatBottomSheetOpened -> {
                if (event.isOpened) {
                    connectToChat()
                } else {
                    disconnectFromChat()
                }
            }

            is TranslationEvent.KickUser -> {
                kickUser(event)
            }

            TranslationEvent.Reconnect -> {
                reconnect()
            }

            is TranslationEvent.SendMessage -> {
                sendMessage(event.text)
            }

            TranslationEvent.StartStreaming -> {
                startStreaming()
            }

            TranslationEvent.StopStreaming -> {
                stopStreaming()
            }

            is TranslationEvent.UserBottomSheetOpened -> {
                if (event.isOpened) {
                    reloadMembers()
                }
            }

            is TranslationEvent.UserBottomSheetQueryChanged -> {
                changeUserQuery(event.newQuery)
            }

            TranslationEvent.ToggleCamera -> {
                toggle(toggleCamera = true)
            }

            TranslationEvent.ToggleMicrophone -> {
                toggle(toggleCamera = false)
            }

            TranslationEvent.LowBitrate -> {
                processLowBitrate()
            }

            TranslationEvent.ReconnectAttemptsOver -> {
                reconnectAttemptsOver()
            }

            TranslationEvent.ReconnectAfterAttemptsOver -> {
                reconnectAfterAttemptsOver()
            }

            TranslationEvent.ConnectionSucceed -> {
                handleSucceedConnection()
            }
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun handleSucceedConnection() {
        _translationStreamerUiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.SUCCESS
            )
        }
    }
    private fun reconnectAfterAttemptsOver() {
        disconnectAndDoSmth()
        _translationStreamerUiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.RECONNECTING
            )
        }
        coroutineScope.launch {
            _oneTimeEvent.send(
                TranslationOneTimeEvent.ReconnectAfterOver
            )
        }
    }
    private fun reconnectAttemptsOver() {
        _translationStreamerUiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.NO_CONNECTION
            )
        }
    }
    private fun reconnect() {
        _translationStreamerUiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.RECONNECTING
            )
        }
        coroutineScope.launch {
            _oneTimeEvent.send(
                TranslationOneTimeEvent.Reconnect
            )
        }
    }
    private fun processLowBitrate() {
        coroutineScope.launch {
            _oneTimeEvent.send(
                TranslationOneTimeEvent.ShowWeakConnectionSnackbar
            )
        }
    }
    private fun handleSignal(signal: TranslationSignalModel) {
        if (signal.signal == TranslationSignalTypeModel.MICROPHONE) {
            if (!signal.value) {
                coroutineScope.launch {
                    _oneTimeEvent.send(
                        TranslationOneTimeEvent.ShowMicroDisabledSnackbar
                    )
                }
            }
        }
    }

    private fun toggle(toggleCamera: Boolean) {
        translationInfo { info ->
            coroutineScope.launch {
                translationRepository.sendSignal(
                    translationId = info.id,
                    signalType = if (toggleCamera) TranslationSignalTypeModel.CAMERA
                    else TranslationSignalTypeModel.MICROPHONE,
                    value = if (toggleCamera) !info.camera else !info.microphone
                ).on(
                    success = {
                        _translationStreamerUiState.update {
                            it.copy(
                                translationInfo = it.translationInfo?.copy(
                                    camera = if (toggleCamera) !info.camera else info.camera,
                                    microphone = if (toggleCamera) info.microphone else !info.microphone
                                )
                            )
                        }
                        _oneTimeEvent.send(
                            if (toggleCamera) TranslationOneTimeEvent.ToggleCamera(value = !info.camera)
                            else TranslationOneTimeEvent.ToggleMicrophone(value = !info.microphone)
                        )
                    },
                    error = {},
                    loading = {}
                )
            }
        }
    }

    private fun changeUiToStream() {
        _translationStreamerUiState.update {
            it.copy(
                translationStatus = TranslationStreamerStatus.STREAM
            )
        }
    }

    private fun changeUiToPreview() {
        _translationStreamerUiState.update {
            it.copy(
                translationStatus = TranslationStreamerStatus.PREVIEW
            )
        }
    }

    private fun sendMessage(message: String) {
        translationInfo { translation ->
            coroutineScope.launch {
                translationRepository.sendMessage(
                    translationId = translation.id,
                    text = message
                )
            }
        }
    }

    private fun changeUserQuery(query: String) {
        _query.value = query
    }

    private fun stopStreaming() {
        coroutineScope.launch {
            translationRepository.disconnectFromTranslation()
            stopPinging()
        }
    }

    private fun startStreaming() {
        translationInfo { translation ->
            coroutineScope.launch {
                connectSocket(translation.id)
                startPinging()
                startDurationTimer()
            }
        }
    }

    private fun reloadMembers() {
        reloadMembers.value = !reloadMembers.value
    }

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

    private fun kickUser(event: TranslationEvent.KickUser) {
        translationInfo { translation ->
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

    private fun changeFacing() {
        _translationStreamerUiState.update {
            it.copy(
                facing = if (it.facing == Facing.BACK) Facing.FRONT else Facing.BACK
            )
        }
    }

    private fun extendTranslation(translationId: String, appendMinutes: Long) {
        if (_translationStreamerUiState.value.translationStatus == TranslationStreamerStatus.EXPIRED) {
            _translationStreamerUiState.update {
                it.copy(
                    onPreviewFromExpired = true,
                    translationStatus = TranslationStreamerStatus.PREVIEW
                )
            }
            coroutineScope.launch {
                _oneTimeEvent.send(
                    TranslationOneTimeEvent.FromExpiredToPreview
                )
            }
        }
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

    private fun completeTranslation() {
        translationInfo { translation ->
            coroutineScope.launch {
                translationRepository.endTranslation(
                    translationId = translation.id
                )
            }
        }
        _translationStreamerUiState.update {
            it.copy(
                translationStatus = TranslationStreamerStatus.COMPLETED
            )
        }
        coroutineScope.launch {
            _oneTimeEvent.send(
                TranslationOneTimeEvent.CompleteTranslation
            )
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
            _translationStreamerUiState.update {
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
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.TranslationExtended(
                                duration = "$hourString$minuteString:00"
                            )
                        )
                    }
                }
            }
        } ?: kotlin.run {
            coroutineScope.launch {
                _oneTimeEvent.send(
                    TranslationOneTimeEvent.TranslationExtended(
                        duration = null
                    )
                )
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    private fun disconnectAndDoSmth() {
        coroutineScope.launch {
            translationRepository.disconnectFromTranslation().on(
                success = {
                    translationInfo {
                        connectSocket(it.id)
                    }
                },
                loading = {},
                error = {
                    meetInfo {
                        coroutineScope.launch {
                            translationRepository.disconnectWebSocket()
                            Log.d("TEST","Connect")
                            translationRepository.connectWebSocket(
                                userId = it.userId,
                                translationId = it.id
                            )
                        }
                    }
                }
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
                handleUserConnected()
            }

            is TranslationCallbackEvents.SignalReceived -> {
                handleSignal(socketEvent.signal)
            }

            is TranslationCallbackEvents.UserDisconnected -> {
                handleUserDisconnected()
            }

            is TranslationCallbackEvents.UserKicked -> {
                handleUserKicked()
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
        coroutineScope.launch {
            handleAppendTime(duration)
            delay(2000)
            _translationStreamerUiState.update {
                it.copy(
                    translationInfo = it.translationInfo?.copy(
                        status = TranslationStatusModel.ACTIVE,
                        completedAt = completedAt
                    )
                )
            }
        }
    }

    private fun handleMembersCountChanged(count: Int) {
        _translationStreamerUiState.update {
            it.copy(
                membersCount = count
            )
        }
    }

    private fun handleUserKicked() {
        reloadMembers.value = !reloadMembers.value
    }

    private fun handleUserDisconnected() {
        reloadMembers.value = !reloadMembers.value
    }

    private fun handleUserConnected() {
        reloadMembers.value = !reloadMembers.value
    }

    private fun handleTranslationExpired() {
        _translationStreamerUiState.update {
            it.copy(
                translationStatus = TranslationStreamerStatus.EXPIRED
            )
        }
        coroutineScope.launch {
            _oneTimeEvent.send(
                TranslationOneTimeEvent.TranslationExpired
            )
        }
    }

    private fun handleBroadcastCompletion() {
        _translationStreamerUiState.update {
            it.copy(
                translationStatus = TranslationStreamerStatus.COMPLETED
            )
        }
        coroutineScope.launch {
            _oneTimeEvent.send(
                TranslationOneTimeEvent.CompleteTranslation
            )
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                    _translationStreamerUiState.update {
                        it.copy(
                            translationInfo = translation,
                            translationStatus = when (translation.status) {
                                TranslationStatusModel.INACTIVE -> TranslationStreamerStatus.PREVIEW
                                TranslationStatusModel.ACTIVE -> TranslationStreamerStatus.PREVIEW
                                TranslationStatusModel.EXPIRED -> TranslationStreamerStatus.EXPIRED
                                TranslationStatusModel.COMPLETED -> TranslationStreamerStatus.COMPLETED
                            }
                        )
                    }
                },
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
                success = { meetingModel ->
                    _translationStreamerUiState.update {
                        it.copy(
                            meetingModel = meetingModel
                        )
                    }
                },
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
      UTILS
     */
    private fun translationInfo(
        block: (TranslationInfoModel) -> Unit
    ) {
        _translationStreamerUiState.value.translationInfo?.let { info ->
            block(info)
        }
    }

    private fun meetInfo(
        block: (FullMeetingModel) -> Unit
    ) {
        _translationStreamerUiState.value.meetingModel?.let { info ->
            block(info)
        }
    }
}