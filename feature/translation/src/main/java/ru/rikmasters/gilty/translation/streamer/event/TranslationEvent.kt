package ru.rikmasters.gilty.translation.streamer.event

import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.translation.streamer.model.RTMPStatus
import ru.rikmasters.gilty.translation.streamer.model.SurfaceState

sealed interface TranslationEvent {
    data class Initialize(val meetingId: String) : TranslationEvent
    object Dismiss : TranslationEvent
    object EnterBackground : TranslationEvent
    data class EnterForeground(val meetingId: String) : TranslationEvent
    data class ProcessRTMPStatus(val status: RTMPStatus) : TranslationEvent
    object LowBitrate : TranslationEvent
    object Reconnect : TranslationEvent
    object BitrateStabilized : TranslationEvent
    object DecreaseRetryCount : TranslationEvent
    data class ChangeSurfaceState(val state:SurfaceState) : TranslationEvent
    object CompleteTranslation : TranslationEvent



    data class UserBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class ChatBottomSheetOpened(val isOpened: Boolean) : TranslationEvent
    data class UserBottomSheetQueryChanged(val newQuery: String) : TranslationEvent
    data class SendMessage(val text: String) : TranslationEvent
    data class KickUser(val user: FullUserModel) : TranslationEvent
    data class AppendTranslation(val appendMinutes: Int) : TranslationEvent
    object ChangeFacing : TranslationEvent

    object ToggleCamera : TranslationEvent
    object ToggleMicrophone : TranslationEvent

}