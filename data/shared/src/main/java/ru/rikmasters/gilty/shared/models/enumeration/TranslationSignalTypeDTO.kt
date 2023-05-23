package ru.rikmasters.gilty.shared.models.enumeration

import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel

enum class TranslationSignalTypeDTO {
    CAMERA, MICROPHONE;

    companion object {
        fun map(domainModel: TranslationSignalTypeModel) = when (domainModel) {
            TranslationSignalTypeModel.CAMERA -> CAMERA
            TranslationSignalTypeModel.MICROPHONE -> MICROPHONE
        }

        fun map(dto: TranslationSignalTypeDTO) = when (dto) {
            MICROPHONE -> TranslationSignalTypeModel.MICROPHONE
            CAMERA -> TranslationSignalTypeModel.CAMERA
        }
    }
}