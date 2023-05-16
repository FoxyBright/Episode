package ru.rikmasters.gilty.translation.event

sealed interface TranslationEvent {
    data class EnterScreen(val meetingId: String) : TranslationEvent
    /*
    object ConnectToTranslation : TranslationEvent
    object DisconnectFromTranslation : TranslationEvent
    object ConnectToTranslationChat : TranslationEvent
    object DisconnectFromTranslationChat : TranslationEvent
     */
    object ChangeFacing : TranslationEvent
    object StartStreaming : TranslationEvent
    object StopStreaming : TranslationEvent
    object ChangeMicrophoneState : TranslationEvent
    object ChangeVideoState : TranslationEvent
}