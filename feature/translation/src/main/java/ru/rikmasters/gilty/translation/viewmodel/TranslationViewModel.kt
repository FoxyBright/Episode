package ru.rikmasters.gilty.translation.viewmodel

import android.util.Log
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
import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel
import ru.rikmasters.gilty.shared.model.enumeration.TranslationStatusModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translation.event.TranslationEvent
import ru.rikmasters.gilty.translation.event.TranslationOneTimeEvent
import ru.rikmasters.gilty.translation.model.Facing
import ru.rikmasters.gilty.translation.model.TranslationUiState
import ru.rikmasters.gilty.translations.model.TranslationCallbackEvents
import ru.rikmasters.gilty.translations.repository.TranslationRepository

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()

    private val connected = MutableStateFlow(false)

    private val translationInfo = MutableStateFlow<TranslationInfoModel?>(null)

    private val _translationUiState = MutableStateFlow(TranslationUiState())
    val translationUiState = _translationUiState.asStateFlow()

    private val _oneTimeEvent = Channel<TranslationOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    init {
        // Pinging while connected
        coroutineScope.launch {
            connected.collectLatest { connected ->
                translationInfo.value?.let { translation ->
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
                coroutineScope.launch {
                    translationRepository.getTranslationInfo(
                        translationId = event.translationId
                    ).on(
                        loading = {
                            Log.d("TEST", "Loading")
                            _translationUiState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        },
                        success = { translation ->
                            Log.d("TEST", "Success $translation")
                            _translationUiState.update {
                                it.copy(
                                    isLoading = false,
                                    translationInfo = translation
                                )
                            }
                        },
                        error = { cause ->
                            Log.d("TEST", "Error $cause")
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

            TranslationEvent.ConnectToTranslation -> {
                coroutineScope.launch {
                    translationInfo.value?.let { translation ->
                        translationRepository.connectToTranslation(
                            translationId = translation.id
                        )
                        startPinging()
                    }
                }
            }

            TranslationEvent.ConnectToTranslationChat -> {
                coroutineScope.launch {
                    translationInfo.value?.let { translation ->
                        translationRepository.connectToTranslationChat(
                            translationId = translation.id
                        )
                    }
                }
            }

            TranslationEvent.DisconnectFromTranslation -> {
                coroutineScope.launch {
                    translationInfo.value?.let {
                        translationRepository.disconnectFromTranslation()
                        stopPinging()
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

            TranslationEvent.ChangeFacing -> {
                _translationUiState.update {
                    it.copy(
                        selectedCamera = if (it.selectedCamera == Facing.BACK) Facing.FRONT else Facing.BACK
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

}