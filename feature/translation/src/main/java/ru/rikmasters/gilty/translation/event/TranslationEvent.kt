package ru.rikmasters.gilty.translation.event

sealed interface TranslationEvent {
    data class EnterScreen(val translationId: String) : TranslationEvent
    object ConnectToTranslation : TranslationEvent
    object DisconnectFromTranslation : TranslationEvent
    object ConnectToTranslationChat : TranslationEvent
    object DisconnectFromTranslationChat : TranslationEvent
    object ChangeFacing : TranslationEvent
    object StartStreaming : TranslationEvent
    object StopStreaming : TranslationEvent
}