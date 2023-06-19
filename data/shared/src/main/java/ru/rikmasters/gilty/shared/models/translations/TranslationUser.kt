package ru.rikmasters.gilty.shared.models.translations

import ru.rikmasters.gilty.shared.models.User

data class TranslationUser(
    val user: User,
    val count: Int
)