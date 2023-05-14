package ru.rikmasters.gilty.shared.models.enumeration

enum class TranslationStatusDTO {
    INACTIVE, ACTIVE, EXPIRED, COMPLETED;

    fun map() = when (this) {
        INACTIVE -> TranslationStatusModel.INACTIVE
        ACTIVE -> TranslationStatusModel.ACTIVE
        EXPIRED -> TranslationStatusModel.EXPIRED
        COMPLETED -> TranslationStatusModel.COMPLETED
    }
}