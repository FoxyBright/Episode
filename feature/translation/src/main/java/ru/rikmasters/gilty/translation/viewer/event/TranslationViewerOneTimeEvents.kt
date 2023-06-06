package ru.rikmasters.gilty.translation.viewer.event

sealed interface TranslationViewerOneTimeEvents {
    data class OnError(val message: String) : TranslationViewerOneTimeEvents
    data class TranslationExtended(val duration: String? = null) : TranslationViewerOneTimeEvents
    data class ConnectToStream(val wsUrl: String) : TranslationViewerOneTimeEvents
    object TranslationResumed : TranslationViewerOneTimeEvents
    object LowConnection : TranslationViewerOneTimeEvents
    object DisconnectWebRtc : TranslationViewerOneTimeEvents
    object MicroOff : TranslationViewerOneTimeEvents
}