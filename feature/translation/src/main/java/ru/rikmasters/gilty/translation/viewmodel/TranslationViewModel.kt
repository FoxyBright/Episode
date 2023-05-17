package ru.rikmasters.gilty.translation.viewmodel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()
    private val meetingRepository: MeetingManager by inject()

    private val connected = MutableStateFlow(false)

    private val _translationUiState = MutableStateFlow(TranslationUiState())
    val translationUiState = _translationUiState.asStateFlow()

    private val _oneTimeEvent = Channel<TranslationOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    private val _remainTime = MutableStateFlow("")
    val remainTime = _remainTime.asStateFlow()

    init {
        coroutineScope.launch {
            connected.collectLatest { connected ->
                _translationUiState.value.translationInfo?.let {
                    val startTime = it.startedAt
                    val endTime = it.completedAt
                    val currentTime = LocalDateTime.now()
                    startTime?.let {
                        if (currentTime.isAfter(startTime)) {
                            var difference = endTime.millis() - currentTime.millis()
                            while (difference > 0) {
                                _remainTime.value = LocalDateTime(difference).format("HH:mm:ss")
                                delay(1000)
                                difference -= 1000
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
                            }

                            is TranslationCallbackEvents.UserDisconnected -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        membersCount = socketAnswers.count
                                    )
                                }
                            }

                            is TranslationCallbackEvents.UserKicked -> {
                                _translationUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        membersCount = socketAnswers.count
                                    )
                                }
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
            /*
            TranslationEvent.ConnectToTranslationChat -> {
                coroutineScope.launch {
                    translationInfo.value?.let { translation ->
                        translationRepository.connectToTranslationChat(
                            translationId = translation.id
                        )
                    }
                }
            }
            TranslationEvent.DisconnectFromTranslationChat -> {
                coroutineScope.launch {
                    translationInfo.value?.let {
                        translationRepository.disconnectFromTranslationChat()
                    }
                }
            }
             */
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