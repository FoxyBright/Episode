package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.DemoOrganizerModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.OrganizerModel
import ru.rikmasters.gilty.shared.model.profile.DemoHiddenPhotoModel
import ru.rikmasters.gilty.shared.model.profile.HiddenPhotoModel

data class RespondModel(
    val id: Int,
    val meet: MeetingModel,
    val comment: String?,
    val sender: OrganizerModel,
    val type: RespondType,
    val hiddenPhoto: List<Pair<HiddenPhotoModel, Boolean>>?
)

val DemoSendRespondsModel = RespondModel(
    1,
    DemoFullMeetingModel,
    null,
    DemoOrganizerModel,
    RespondType.SEND,
    null
)

val DemoReceivedRespondsModel = RespondModel(
    1, DemoFullMeetingModel,
    "Классно выглядишь, пойдем? Я вроде адекватный))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    listOf(
        Pair(DemoHiddenPhotoModel, true),
        Pair(DemoHiddenPhotoModel, true),
        Pair(DemoHiddenPhotoModel, false),
        Pair(DemoHiddenPhotoModel, false)
    )
)

val DemoReceivedRespondModelWithoutPhoto = RespondModel(
    1,
    DemoFullMeetingModel,
    "Покажи свои фото))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    null
)