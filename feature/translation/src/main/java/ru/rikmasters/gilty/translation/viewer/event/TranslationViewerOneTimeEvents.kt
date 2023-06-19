package ru.rikmasters.gilty.translation.viewer.event


sealed interface TranslationViewerOneTimeEvents {
    data class ConnectToStream(val wsUrl: String) : TranslationViewerOneTimeEvents
    object DisconnectWebRtc : TranslationViewerOneTimeEvents
    object ShowSnackbar : TranslationViewerOneTimeEvents
    data class OnError(val message: String) : TranslationViewerOneTimeEvents
}