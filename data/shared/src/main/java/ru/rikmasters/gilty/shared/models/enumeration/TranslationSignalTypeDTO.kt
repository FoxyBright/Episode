package ru.rikmasters.gilty.shared.models.enumeration

import ru.rikmasters.gilty.shared.model.enumeration.TranslationSignalTypeModel

enum class TranslationSignalTypeDTO {
    CAMERA, MICROPHONE;

    fun map(domainModel: TranslationSignalTypeModel) = when (domainModel) {
        TranslationSignalTypeModel.CAMERA -> CAMERA
        TranslationSignalTypeModel.MICROPHONE -> MICROPHONE
    }
}
