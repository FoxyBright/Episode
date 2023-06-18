package ru.rikmasters.gilty.translation.viewer.event

import ru.rikmasters.gilty.translations.webrtc.model.WebRtcAnswer
import ru.rikmasters.gilty.translations.webrtc.model.WebRtcStatus

sealed interface TranslationViewerEvent {
    data class Initialize(val meetingId: String) : TranslationViewerEvent
    object Dismiss : TranslationViewerEvent
    object EnterBackground : TranslationViewerEvent
    data class EnterForeground(val meetingId: String) : TranslationViewerEvent
    data class HandleWebRtcStatus(val status: WebRtcStatus) : TranslationViewerEvent
    data class HandleWebRtcAnswer(val answer: WebRtcAnswer) : TranslationViewerEvent
    object Reconnect : TranslationViewerEvent





    data class MessageSent(val messageText: String) : TranslationViewerEvent
    data class QueryChanged(val newQuery: String) : TranslationViewerEvent

    object ConnectToChat : TranslationViewerEvent
    object DisconnectFromChat : TranslationViewerEvent
    object ReloadMembers : TranslationViewerEvent

}