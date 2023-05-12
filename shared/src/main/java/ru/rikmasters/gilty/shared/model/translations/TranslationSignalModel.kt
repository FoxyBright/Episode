package ru.rikmasters.gilty.shared.model.translations

import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel

data class TranslationSignalModel(
    val id: String,
    val signal: TranslationSignalTypeModel,
    val value: Boolean
)
