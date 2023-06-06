package ru.rikmasters.gilty.translation.viewer.model

import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translation.shared.model.ConnectionStatus

data class TranslationViewerUiState(
    val translationInfo: TranslationInfoModel?= null,
    val meetingModel: FullMeetingModel? = null,
    val membersCount: Int = 0,
    val remainTime: String = "",
    val translationStatus: TranslationViewerStatus? = TranslationViewerStatus.INACTIVE,
    val connectionStatus: ConnectionStatus? = ConnectionStatus.SUCCESS
)