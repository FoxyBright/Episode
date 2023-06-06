package ru.rikmasters.gilty.translation.streamer.event

import ru.rikmasters.gilty.shared.model.meeting.FullUserModel

sealed interface TranslationEvent {
    data class Initialize(val meetingId: String) : TranslationEvent
    object Dismiss : TranslationEvent
    data class UserBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class ChatBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class UserBottomSheetQueryChanged(val newQuery: String) : TranslationEvent
    data class SendMessage(val text: String) : TranslationEvent
    data class KickUser(val user: FullUserModel) : TranslationEvent
    data class AppendTranslation(val appendMinutes: Int) : TranslationEvent
    object ConnectionSucceed : TranslationEvent
    object ChangeFacing : TranslationEvent
    object ChangeUiToStream : TranslationEvent
    object ChangeUiToPreview : TranslationEvent
    object StartStreaming : TranslationEvent
    object StopStreaming : TranslationEvent
    object ReconnectAfterAttemptsOver : TranslationEvent
    object ReconnectAttemptsOver : TranslationEvent
    object Reconnect : TranslationEvent
    object CompleteTranslation : TranslationEvent
    object LowBitrate : TranslationEvent
    object ToggleCamera : TranslationEvent
    object ToggleMicrophone : TranslationEvent

}