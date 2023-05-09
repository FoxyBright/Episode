package ru.rikmasters.gilty.shared.model.translations

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel

data class TranslationMessageModel(
    val id: String,
    val text: String,
    val author: FullUserModel,
    val createdAt: LocalDateTime
)