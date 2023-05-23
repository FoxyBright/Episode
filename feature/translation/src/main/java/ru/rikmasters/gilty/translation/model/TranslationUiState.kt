package ru.rikmasters.gilty.translation.model

import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel

data class TranslationUiState(
    val isLoading: Boolean = false,
    val translationInfo: TranslationInfoModel?= null,
    val membersCount: Int? = 0,
    val translationStatus: TranslationStatus? = TranslationStatus.PREVIEW,
    val selectedCamera: Facing ?= Facing.FRONT,
    val meetingModel: FullMeetingModel?= null,
    val connectionStatus: ConnectionStatus? = ConnectionStatus.SUCCESS
)