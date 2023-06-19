package ru.rikmasters.gilty.translations.model

import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel
import java.time.ZonedDateTime

sealed interface TranslationCallbackEvents {
    object TranslationStarted : TranslationCallbackEvents
    object TranslationCompleted : TranslationCallbackEvents
    object TranslationExpired : TranslationCallbackEvents
    object MessageReceived : TranslationCallbackEvents
    object ConnectionEstablished : TranslationCallbackEvents
    data class SignalReceived(val signal: TranslationSignalModel) : TranslationCallbackEvents
    data class TranslationExtended(val completedAt: ZonedDateTime, val duration: Int) : TranslationCallbackEvents
    data class UserConnected(val user: UserModel) : TranslationCallbackEvents
    data class UserDisconnected(val user: UserModel) : TranslationCallbackEvents
    data class UserKicked(val user: UserModel) : TranslationCallbackEvents
    data class MembersCountChanged(val count: Int) : TranslationCallbackEvents
}