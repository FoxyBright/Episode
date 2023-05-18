package ru.rikmasters.gilty.translation.viewmodel

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
    private val meetingRepository: MeetingManager by inject()

    private val connected = MutableStateFlow(false)

    private val userIsOpened = MutableStateFlow(false)

    private val chatIsOpened = MutableStateFlow(false)

    private val reloadUser = MutableStateFlow(false)
    private val reloadChat = MutableStateFlow(false)

    private val _usersQuery = MutableStateFlow("")
    val usersQuery = _usersQuery.asStateFlow()

    // Открыт ли боттом шит, состояние поиска
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = combine(
        chatIsOpened,
        reloadChat
    ) { isOpened, reload ->
        Pair(isOpened,reload)
    }.flatMapLatest { (isOpened, _) ->
        Log.d("WebSock","FLATMAPPED")
        // Если открыт ботомшит
        if (isOpened) {
            Log.d("WebSock","iSOPENED")
            // Если id трансляции существует
            translationUiState.value.translationInfo?.let { translation ->
                Log.d("WebSock","Translation $translation")
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

    private val _translationUiState = MutableStateFlow(TranslationUiState())
    val translationUiState = _translationUiState.asStateFlow()

    private val _oneTimeEvent = Channel<TranslationOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    private val _remainTime = MutableStateFlow("")
    val remainTime = _remainTime.asStateFlow()

    init {
        // Timer
        coroutineScope.launch {
            connected.collectLatest { connected ->
                if (connected) {
                    _translationUiState.value.translationInfo?.let {
                        val startTime = it.startedAt
                        val endTime = it.completedAt
                        val currentTime = LocalDateTime.nowZ()
                        startTime?.let {
                            if (currentTime.isAfter(startTime)) {
                                var difference = endTime.millis() - currentTime.millis()
                                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                                val time = Date(difference)
                                while (difference > 0) {
                                    _remainTime.value = sdf.format(time)
                                    delay(1000)
                                    difference -= 1000
                                }
                            }
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
                                        )
                                    )
                                }
                            }

                            TranslationCallbackEvents.TranslationExpired -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        translationInfo = it.translationInfo?.copy(
                                            status = TranslationStatusModel.EXPIRED
                                        )
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

            TranslationEvent.StartStreaming -> {
                _translationUiState.update {
                    it.copy(
                        translationStatus = TranslationStatus.STREAM
                    )
                }
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
                _translationUiState.update {
                    it.copy(
                        translationStatus = TranslationStatus.PREVIEW
                    )
                }
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
        }
    }

    private fun startPinging() {
        connected.value = true
    }

    private fun stopPinging() {
        connected.value = false
    }

    private fun getScreenInfo(meetingId: String) {
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