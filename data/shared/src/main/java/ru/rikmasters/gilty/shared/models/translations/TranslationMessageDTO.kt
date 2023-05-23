package ru.rikmasters.gilty.shared.models.translations

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.shared.models.FullUserDTO

data class TranslationMessageDTO(
    val id: String,
    val text: String,
    val author: FullUserDTO,
    val createdAt: String,
) {
    fun map() = TranslationMessageModel(
        id = id,
        text = text,
        author = author.map(),
        createdAt = LocalDateTime.of(createdAt),
    )
}