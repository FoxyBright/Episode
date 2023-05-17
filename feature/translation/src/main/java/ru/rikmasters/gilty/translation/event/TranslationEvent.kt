package ru.rikmasters.gilty.translation.event

import ru.rikmasters.gilty.shared.model.meeting.FullUserModel

sealed interface TranslationEvent {
    data class EnterScreen(val meetingId: String) : TranslationEvent
    data class UserBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class UserBottomSheetQueryChanged(val newQuery: String) : TranslationEvent
    data class KickUser(val user: FullUserModel) : TranslationEvent
    object ChangeFacing : TranslationEvent
    object StartStreaming : TranslationEvent
    object StopStreaming : TranslationEvent
    object ChangeMicrophoneState : TranslationEvent
    object ChangeVideoState : TranslationEvent

}