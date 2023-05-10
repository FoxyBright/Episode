package ru.rikmasters.gilty.shared.models.translations

import ru.rikmasters.gilty.shared.models.enumeration.TranslationSignalTypeDTO

data class TranslationSignalDTO(
    val id: String,
    val signal: TranslationSignalTypeDTO,
    val value: Boolean
)