package ru.rikmasters.gilty.event

sealed interface TranslationOneTimeEvent {
    data class ErrorHappened(val errorMessage: String) : TranslationOneTimeEvent
}