package ru.rikmasters.gilty.translations.model

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.models.translations.TranslationSignalDTO

sealed interface TranslationCallbackEvents {
    object TranslationStarted : TranslationCallbackEvents
    object TranslationCompleted : TranslationCallbackEvents
    object TranslationExpired : TranslationCallbackEvents
    data class SignalReceived(val signal: TranslationSignalDTO) : TranslationCallbackEvents
    data class TranslationExtended(val completedAt: LocalDateTime, val duration: Int) : TranslationCallbackEvents
    data class UserConnected(val user: String) : TranslationCallbackEvents
    data class UserDisconnected(val user: String) : TranslationCallbackEvents
    data class UserKicked(val user: String) : TranslationCallbackEvents
}