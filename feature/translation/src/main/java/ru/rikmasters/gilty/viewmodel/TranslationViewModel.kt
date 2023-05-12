package ru.rikmasters.gilty.viewmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.event.TranslationEvent
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translations.repository.TranslationRepository

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()

    private val pinging = MutableStateFlow(false)

    private val translationInfo = MutableStateFlow<TranslationInfoModel?>(null)

    init {
        coroutineScope.launch {
            pinging.collectLatest { isPinging ->
                translationInfo.value?.let { translation ->
                    while (isPinging) {
                        translationRepository.ping(
                            translationId = translation.id
                        )
                        delay(8000)
                    }
                }
            }
        }
    }

    fun onEvent(event: TranslationEvent) {
        when(event) {
            is TranslationEvent.EnterScreen -> {
                coroutineScope.launch {
                    translationRepository.getTranslationInfo(
                        translationId = event.translationId
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
        }
    }

    private fun startPinging() {
        pinging.value = true
    }

    private fun stopPinging() {
        pinging.value = false
    }

}
