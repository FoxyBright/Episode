package ru.rikmasters.gilty.translation.viewmodel

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
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationStatus
import ru.rikmasters.gilty.translation.model.TranslationUiState
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.repository.TranslationRepository

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
    private val meetingRepository: MeetingManager by inject()

    // Идет ли стрим
    private val connected = MutableStateFlow(false)
    // Открыт ли ботомшит с пользователями
    private val userIsOpened = MutableStateFlow(false)
    // Открыт ли чат
    private val chatIsOpened = MutableStateFlow(false)
    // Перезагрузка пользователей ( Когда пришли новые с сокетов )
    private val reloadUser = MutableStateFlow(false)
    // Перезагрузка сообщений чата ( Когда пришли новые с сокетов )
    private val reloadChat = MutableStateFlow(false)
    // Строка поиска в ботомшите пользователей
    private val _usersQuery = MutableStateFlow("")
    val usersQuery = _usersQuery.asStateFlow()

    // Открыт ли боттом шит, состояние поиска
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = combine(
        chatIsOpened,
        reloadChat
    ) { isOpened, reload ->
        Pair(isOpened, reload)
    }.flatMapLatest { (isOpened, _) ->
        // Если открыт ботомшит
        if (isOpened) {
            // Если id трансляции существует
            translationUiState.value.translationInfo?.let { translation ->
                translationRepository.getMessages(
                    translationId = translation.id
                )
            } ?: flow { }
        } else {
            // БотомШит закрыт
            flow { }
        }
    }.cachedIn(coroutineScope)

    // Открыт ли боттом шит, состояние поиска
    @OptIn(ExperimentalCoroutinesApi::class)
    val connectedUsers = combine(
        userIsOpened,
        _usersQuery,
        reloadUser
    ) { isOpened, query, reload ->
        Triple(isOpened, query, reload)
    }.flatMapLatest { (isOpened, query, _) ->
        // Если открыт ботомшит
        if (isOpened) {
            // Если id трансляции существует
            translationUiState.value.translationInfo?.let { translation ->
                translationRepository.getConnectedUsers(
                    translationId = translation.id,
                    query = query
                )
            } ?: flow { }
        } else {
            // БотомШит закрыт
            flow { }
        }
    }.cachedIn(coroutineScope)

    // Состояние трансляции
    private val _translationUiState = MutableStateFlow(TranslationUiState())
    val translationUiState = _translationUiState.asStateFlow()
    // Канал одноразовых ивентов
    private val _oneTimeEvent = Channel<TranslationOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()
    // Оставшееся время трансляции для таймера
    private val _remainTime = MutableStateFlow("")
    val remainTime = _remainTime.asStateFlow()
    // Нужно ли красить градиентом таймер
    private val _greenHighlightTimer = MutableStateFlow(false)
    // Стэйт передающийся в UI с делэем в 2 секунды превращается в ""
    private val _addTimer = MutableStateFlow("")
    val addTimer = _addTimer.asStateFlow()
    // Стэйт передающийся в UI с делэем в 2 секунды превращается в false
    private val _highlightTimer = MutableStateFlow(false)
    val highlightTimer = _highlightTimer.asStateFlow()
    // Было ли время трансляции обновлено через кнопку таймера на экране
    private val extendFromTimer = MutableStateFlow(true)
    // Снэкбар возобновления трансляции
    // TODO: Использовать его
    private val _translationResumedSnackbar = MutableStateFlow(false)
    val translationResumedSnackbar = _translationResumedSnackbar.asStateFlow()
    // Обновить таймер
    private val refreshTimer = MutableStateFlow(false)

    init {
        // Timer
        coroutineScope.launch {
            combine(
                refreshTimer,
                connected,
                _translationUiState
            ) { refreshTimer, connected, ui ->
                Triple(refreshTimer, connected, ui)
            }.collectLatest { (_, connected, ui) ->
                if (connected) {
                    ui.translationInfo?.let {
                        var currTime = LocalDateTime.nowZ()
                        while (currTime.isBefore(it.completedAt)) {
                            val duration = Duration.between(
                                currTime, it.completedAt
                            )
                            val hours = (duration / 3600000)
                            val minutes = (duration - (hours * 3600000))/60000
                            val seconds = (duration - (hours * 3600000) - (minutes * 60000))/1000
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
                            _remainTime.value = "$hourString$minuteString:$secondString"
                            currTime = LocalDateTime.nowZ()
                            delay(1000)
                        }
                    }
                }
            }
        }
        // Pinging while connected
        coroutineScope.launch {
            connected.collectLatest { connected ->
                _translationUiState.value.translationInfo?.let { translation ->
                    while (connected) {
                        translationRepository.ping(
                            translationId = translation.id
                        )
                        delay(8000)
                    }
                }
            }
        }
        // Collect sockets while connected
        coroutineScope.launch {
            connected.collectLatest { connected ->
                if (connected) {
                    translationRepository.webSocketFlow.collectLatest { socketAnswers ->
                        when (socketAnswers) {
                            is TranslationCallbackEvents.SignalReceived -> {
                                when (socketAnswers.signal.signal) {
                                    TranslationSignalTypeModel.MICROPHONE -> {
                                        _translationUiState.update {
                                            it.copy(
                                                isLoading = false,
                                                translationInfo = it.translationInfo?.copy(
                                                    microphone = socketAnswers.signal.value
                                                )
                                            )
                                        }
                                    }

                                    TranslationSignalTypeModel.CAMERA -> {
                                        _translationUiState.update {
                                            it.copy(
                                                isLoading = false,
                                                translationInfo = it.translationInfo?.copy(
                                                    camera = socketAnswers.signal.value
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            TranslationCallbackEvents.TranslationCompleted -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        translationInfo = it.translationInfo?.copy(
                                            status = TranslationStatusModel.COMPLETED
                                        ),
                                        translationStatus = TranslationStatus.COMPLETED
                                    )
                                }
                            }

                            TranslationCallbackEvents.TranslationExpired -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        translationInfo = it.translationInfo?.copy(
                                            status = TranslationStatusModel.EXPIRED
                                        ),
                                        translationStatus = TranslationStatus.EXPIRED
                                    )
                                }
                            }

                            is TranslationCallbackEvents.TranslationExtended -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        translationInfo = it.translationInfo?.copy(
                                            status = TranslationStatusModel.ACTIVE,
                                            completedAt = socketAnswers.completedAt
                                        )
                                    )
                                }
                                if (!extendFromTimer.value) {
                                    _translationUiState.update {
                                        it.copy(
                                            translationStatus = TranslationStatus.PREVIEW
                                        )
                                    }
                                    _greenHighlightTimer.value = true
                                } else {
                                    _greenHighlightTimer.value = false
                                }
                            }

                            TranslationCallbackEvents.TranslationStarted -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        translationInfo = it.translationInfo?.copy(
                                            status = TranslationStatusModel.ACTIVE
                                        )
                                    )
                                }
                            }

                            is TranslationCallbackEvents.UserConnected -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        membersCount = socketAnswers.count
                                    )
                                }
                                reloadUser.value = !reloadUser.value
                            }

                            is TranslationCallbackEvents.UserDisconnected -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        membersCount = socketAnswers.count
                                    )
                                }
                                reloadUser.value = !reloadUser.value
                            }

                            is TranslationCallbackEvents.UserKicked -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        membersCount = socketAnswers.count
                                    )
                                }
                                reloadUser.value = !reloadUser.value
                            }

                            TranslationCallbackEvents.MessageReceived -> {
                                reloadChat.value = !reloadChat.value
                            }
                        }
                    }
                }
            }
        }
    }
    fun onEvent(event: TranslationEvent) {
        when (event) {
            is TranslationEvent.EnterScreen -> {
                getScreenInfo(
                    meetingId = event.meetingId
                )
            }

            TranslationEvent.ChangeFacing -> {
                _translationUiState.update {
                    it.copy(
                        selectedCamera = if (it.selectedCamera == Facing.BACK) Facing.FRONT else Facing.BACK
                    )
                }
            }

            TranslationEvent.ChangeUiToPreview -> {
                _translationUiState.update {
                    it.copy(
                        translationStatus = TranslationStatus.PREVIEW
                    )
                }
            }

            TranslationEvent.ChangeUiToStream -> {
                _translationUiState.update {
                    it.copy(
                        translationStatus = TranslationStatus.STREAM
                    )
                }
                coroutineScope.launch {
                    if (_greenHighlightTimer.value) {
                        _highlightTimer.value = true
                        _translationResumedSnackbar.value = true
                        delay(2000)
                        _highlightTimer.value = false
                        _translationResumedSnackbar.value = false
                        refreshTimer.value = !refreshTimer.value
                    }
                }
            }

            TranslationEvent.StartStreaming -> {
                coroutineScope.launch {
                    _translationUiState.value.translationInfo?.let { translation ->
                        translationRepository.connectToTranslation(
                            translationId = translation.id
                        )
                        startPinging()
                    }
                }
            }

            TranslationEvent.StopStreaming -> {
                coroutineScope.launch {
                    translationRepository.disconnectFromTranslation()
                    stopPinging()
                }
            }

            TranslationEvent.ChangeMicrophoneState -> {
                _translationUiState.value.translationInfo?.let { translation ->
                    coroutineScope.launch {
                        translationRepository.sendSignal(
                            translationId = translation.id,
                            signalType = TranslationSignalTypeModel.MICROPHONE,
                            value = translation.microphone?.let { !it } ?: true
                        )
                    }
                }
            }

            TranslationEvent.ChangeVideoState -> {
                _translationUiState.value.translationInfo?.let { translation ->
                    coroutineScope.launch {
                        translationRepository.sendSignal(
                            translationId = translation.id,
                            signalType = TranslationSignalTypeModel.CAMERA,
                            value = translation.camera?.let { !it } ?: true
                        )
                    }
                }
            }

            is TranslationEvent.UserBottomSheetOpened -> {
                // Ботом шит участников трансляции
                userIsOpened.value = event.isOpened
            }

            is TranslationEvent.UserBottomSheetQueryChanged -> {
                // Состояние поиска ботом шита участников трансляции
                _usersQuery.value = event.newQuery
            }

            is TranslationEvent.KickUser -> {
                coroutineScope.launch {
                    _translationUiState.value.translationInfo?.let { translation ->
                        event.user.id?.let { userId ->
                            translationRepository.kickUser(
                                translationId = translation.id,
                                userId = userId
                            )
                        }
                    }
                }
            }

            is TranslationEvent.ChatBottomSheetOpened -> {
                chatIsOpened.value = event.isOpened
                coroutineScope.launch {
                    if (event.isOpened) {
                        _translationUiState.value.translationInfo?.let { translation ->
                            translationRepository.connectToTranslationChat(
                                translationId = translation.id
                            )
                        }
                    } else {
                        translationRepository.disconnectFromTranslationChat()
                    }
                }
            }

            is TranslationEvent.SendMessage -> {
                coroutineScope.launch {
                    _translationUiState.value.translationInfo?.let { translation ->
                        translationRepository.sendMessage(
                            translationId = translation.id,
                            text = event.text
                        )
                    }
                }
            }

            is TranslationEvent.UpdateConnectionStatus -> {
                _translationUiState.update {
                    it.copy(
                        connectionStatus = event.connectionStatus
                    )
                }
            }

            TranslationEvent.Reconnect -> {
                _translationUiState.value.meetingModel?.id?.let { meetId ->
                    getScreenInfo(
                        meetingId = meetId,
                        isReconnect = true
                    )
                }
            }

            is TranslationEvent.AppendTranslation -> {
                _translationUiState.value.translationInfo?.id?.let { translationId ->
                    coroutineScope.launch {
                        extendTranslation(
                            translationId = translationId,
                            appendMinutes = (event.appendMinutes).toLong()
                        )
                        extendFromTimer.value = false
                    }
                }
            }

            is TranslationEvent.AppendFromTimer -> {
                _translationUiState.value.translationInfo?.id?.let { translationId ->
                    coroutineScope.launch {
                        extendFromTimer.value = true
                        val hour = (event.appendMinutes / 60).takeIf { it > 0 }
                        val hourString = hour?.let { "$hour:" } ?: ""
                        val minute = hour?.let { event.appendMinutes - 60 } ?: event.appendMinutes
                        _addTimer.value = "+ $hourString$minute:00"
                        delay(3000)
                        extendTranslation(
                            translationId = translationId,
                            appendMinutes = (event.appendMinutes).toLong()
                        )
                        _addTimer.value = ""
                    }
                }
            }

            TranslationEvent.CompleteTranslation -> {
                _translationUiState.value.translationInfo?.id?.let { translationId ->
                    stopPinging()
                    coroutineScope.launch {
                        translationRepository.endTranslation(
                            translationId = translationId
                        )
                    }
                }
                _translationUiState.update {
                    it.copy(
                        translationStatus = TranslationStatus.COMPLETED
                    )
                }
            }
        }
    }

    private fun startPinging() {
        connected.value = true
    }

    private fun stopPinging() {
        connected.value = false
    }

    private fun extendTranslation(translationId: String, appendMinutes: Long) {
        coroutineScope.launch {
            translationRepository.extendTranslation(
                translationId = translationId,
                duration = appendMinutes
            ).on(
                loading = {
                    _translationUiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                },
                success = { translation ->
                    _translationUiState.update {
                        it.copy(
                            isLoading = false,
                            translationInfo = translation
                        )
                    }
                },
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.ErrorHappened(
                                errorMessage = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.ErrorHappened(
                                errorMessage = it
                            )
                        )
                    }
                }
            )
        }
    }

    private fun getScreenInfo(meetingId: String, isReconnect: Boolean = false) {
        coroutineScope.launch {
            translationRepository.getTranslationInfo(
                translationId = meetingId
            ).on(
                loading = {
                    _translationUiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                },
                success = { translation ->
                    _translationUiState.update {
                        it.copy(
                            isLoading = false,
                            translationInfo = translation
                        )
                    }
                    if (isReconnect) {
                        _translationUiState.update {
                            it.copy(
                                isLoading = false,
                                translationInfo = translation
                            )
                        }
                        translationRepository.connectToTranslation(
                            translationId = translation.id
                        )
                        startPinging()
                        _oneTimeEvent.send(TranslationOneTimeEvent.Reconnect)
                    }
                },
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.ErrorHappened(
                                errorMessage = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.ErrorHappened(
                                errorMessage = it
                            )
                        )
                    }
                }
            )
            meetingRepository.getDetailedMeetTest(
                meetId = meetingId
            ).on(
                loading = {
                    _translationUiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                },
                success = { meetingModel ->
                    _translationUiState.update {
                        it.copy(
                            isLoading = false,
                            meetingModel = meetingModel
                        )
                    }
                },
                error = { cause ->
                    cause.serverMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.ErrorHappened(
                                errorMessage = it
                            )
                        )
                    } ?: cause.defaultMessage?.let {
                        _oneTimeEvent.send(
                            TranslationOneTimeEvent.ErrorHappened(
                                errorMessage = it
                            )
                        )
                    }
                }
            )
        }
    }
}