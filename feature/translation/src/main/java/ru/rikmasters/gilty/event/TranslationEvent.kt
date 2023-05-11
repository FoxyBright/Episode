package ru.rikmasters.gilty.event

sealed interface TranslationEvent {
    data class ConnectToTranslation(val translationId: String): TranslationEvent
    data class DisconnectFromTranslation(val translationId: String): TranslationEvent
    data class ConnectToTranslationChat(val translationId: String): TranslationEvent
    data class DisconnectFromTranslationChat(val translationId: String): TranslationEvent
}