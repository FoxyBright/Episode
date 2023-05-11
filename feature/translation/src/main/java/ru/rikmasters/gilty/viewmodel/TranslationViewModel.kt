package ru.rikmasters.gilty.viewmodel

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.event.TranslationEvent
import ru.rikmasters.gilty.translations.repository.TranslationRepository

class TranslationViewModel : ViewModel() {

    private val translationRepository: TranslationRepository by inject()

    fun onEvent(event: TranslationEvent) {
        when(event) {
            is TranslationEvent.ConnectToTranslation -> {
                coroutineScope.launch {
                    translationRepository.connectToTranslation(
                        translationId = event.translationId
                    )
                }
            }
            is TranslationEvent.ConnectToTranslationChat -> {
                coroutineScope.launch {
                    translationRepository.connectToTranslationChat(
                        translationId = event.translationId
                    )
                }
            }
            is TranslationEvent.DisconnectFromTranslation -> {
                coroutineScope.launch {
                    translationRepository.disconnectFromTranslation()
                }
            }
            is TranslationEvent.DisconnectFromTranslationChat -> {
                coroutineScope.launch {
                    translationRepository.disconnectFromTranslationChat()
                }
            }
        }
    }

}
