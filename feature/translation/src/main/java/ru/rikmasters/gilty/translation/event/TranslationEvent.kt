package ru.rikmasters.gilty.translation.event

import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.translation.model.ConnectionStatus

sealed interface TranslationEvent {
    data class EnterScreen(val meetingId: String) : TranslationEvent
    data class UserBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class ChatBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class UserBottomSheetQueryChanged(val newQuery: String) : TranslationEvent
    data class SendMessage(val text: String) : TranslationEvent
    data class KickUser(val user: FullUserModel) : TranslationEvent
    data class UpdateConnectionStatus(val connectionStatus: ConnectionStatus) : TranslationEvent
    object ChangeFacing : TranslationEvent
    object ChangeUiToStream : TranslationEvent
    object ChangeUiToPreview : TranslationEvent
    object StartStreaming : TranslationEvent
    object StopStreaming : TranslationEvent
    object ChangeMicrophoneState : TranslationEvent
    object ChangeVideoState : TranslationEvent
    object Reconnect :  TranslationEvent
}