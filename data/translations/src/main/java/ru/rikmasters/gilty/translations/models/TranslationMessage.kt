package ru.rikmasters.gilty.translations.models

data class TranslationMessage(
    val id: String,
    val text: String,
    val author: TranslationMessageAuthor,
    val createdAt: String
)