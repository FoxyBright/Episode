package ru.rikmasters.gilty.model

import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel

data class TranslationUiState(
    val isLoading: Boolean,
    val translationInfo: TranslationInfoModel
)