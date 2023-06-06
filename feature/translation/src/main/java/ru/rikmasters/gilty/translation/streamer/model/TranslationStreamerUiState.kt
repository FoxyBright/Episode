package ru.rikmasters.gilty.translation.streamer.model

import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.translations.TranslationInfoModel
import ru.rikmasters.gilty.translation.shared.model.ConnectionStatus

data class TranslationStreamerUiState(
    val translationInfo: TranslationInfoModel?= null,
    val meetingModel: FullMeetingModel?= null,
    val membersCount: Int = 0,
    val remainTime: String = "",
    val onPreviewFromExpired: Boolean = false,
    val facing: Facing = Facing.FRONT,
    val translationStatus: TranslationStreamerStatus? = TranslationStreamerStatus.PREVIEW,
    val connectionStatus: ConnectionStatus? = ConnectionStatus.SUCCESS
)