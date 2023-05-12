package ru.rikmasters.gilty.translations.model

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.translations.TranslationSignalModel

sealed interface TranslationCallbackEvents {
    object TranslationStarted : TranslationCallbackEvents
    object TranslationCompleted : TranslationCallbackEvents
    object TranslationExpired : TranslationCallbackEvents
    data class SignalReceived(val signal: TranslationSignalModel) : TranslationCallbackEvents
    data class TranslationExtended(val completedAt: LocalDateTime, val duration: Int) : TranslationCallbackEvents
    data class UserConnected(val user: String, val count: Int) : TranslationCallbackEvents
    data class UserDisconnected(val user: String, val count: Int) : TranslationCallbackEvents
    data class UserKicked(val user: String, val count: Int) : TranslationCallbackEvents
}