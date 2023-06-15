package ru.rikmasters.gilty.translation.streamer

import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

sealed interface TranslationOneTimeEvent {
    data class ToggleCamera(val value: Boolean) : TranslationOneTimeEvent
    data class ToggleMicrophone(val value: Boolean) : TranslationOneTimeEvent
    object CompleteTranslation : TranslationOneTimeEvent
    data class ChangeFacing(val facing: StreamerFacing) : TranslationOneTimeEvent
    data class OnError(val message: String) : TranslationOneTimeEvent
    object ShowSnackbar : TranslationOneTimeEvent
    object Reconnect : TranslationOneTimeEvent
    data class StartStreaming(val url: String) : TranslationOneTimeEvent
    object DestroyRTMP : TranslationOneTimeEvent

}