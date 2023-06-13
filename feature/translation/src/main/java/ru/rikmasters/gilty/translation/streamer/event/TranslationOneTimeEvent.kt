package ru.rikmasters.gilty.translation.streamer.event

import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing

sealed interface TranslationOneTimeEvent {
    data class StartStream(val rtmpUrl: String) : TranslationOneTimeEvent
    data class ToggleCamera(val value: Boolean) : TranslationOneTimeEvent
    data class ToggleMicrophone(val value: Boolean) : TranslationOneTimeEvent
    object CompleteTranslation : TranslationOneTimeEvent
    data class ChangeFacing(val facing: StreamerFacing) : TranslationOneTimeEvent
    data class OnError(val message: String) : TranslationOneTimeEvent
    object ShowSnackbar : TranslationOneTimeEvent

}