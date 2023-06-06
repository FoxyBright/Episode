package ru.rikmasters.gilty.translation.streamer.event

sealed interface TranslationOneTimeEvent {
    data class OnError(val message: String) : TranslationOneTimeEvent
    data class TranslationExtended(val duration: String? = null) : TranslationOneTimeEvent
    object CompleteTranslation : TranslationOneTimeEvent
    object FromExpiredToPreview : TranslationOneTimeEvent
    object TranslationExpired : TranslationOneTimeEvent
    object ReconnectAfterOver : TranslationOneTimeEvent
    object Reconnect : TranslationOneTimeEvent
    object ShowWeakConnectionSnackbar : TranslationOneTimeEvent
    object ShowMicroDisabledSnackbar : TranslationOneTimeEvent
    data class ToggleCamera(val value: Boolean) : TranslationOneTimeEvent
    data class ToggleMicrophone(val value: Boolean) : TranslationOneTimeEvent
}