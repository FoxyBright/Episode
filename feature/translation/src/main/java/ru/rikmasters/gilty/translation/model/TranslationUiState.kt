package ru.rikmasters.gilty.translation.model

import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel

data class TranslationUiState(
    val isLoading: Boolean = false,
    val translationInfo: TranslationInfoModel?= null,
    val membersCount: Int? = 0
)