package ru.rikmasters.gilty.translation.event

sealed interface TranslationOneTimeEvent {
    data class ErrorHappened(val errorMessage: String) : TranslationOneTimeEvent
    object Reconnect : TranslationOneTimeEvent
}