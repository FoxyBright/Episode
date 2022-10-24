package ru.rikmasters.gilty.presentation.model.notification

import ru.rikmasters.gilty.presentation.model.enumeration.RespondType
import ru.rikmasters.gilty.presentation.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoOrganizerModel
import ru.rikmasters.gilty.presentation.model.meeting.MeetingModel
import ru.rikmasters.gilty.presentation.model.meeting.OrganizerModel
import ru.rikmasters.gilty.presentation.model.profile.DemoHiddenPhotoModel
import ru.rikmasters.gilty.presentation.model.profile.HiddenPhotoModel

data class RespondModel(
    val id: Int,
    val meet: MeetingModel,
    val comment: String?,
    val sender: OrganizerModel,
    val type: RespondType,
    val hiddenPhoto: List<HiddenPhotoModel>?
)

val DemoSendRespondsModel = RespondModel(
    1,
    DemoMeetingModel,
    null,
    DemoOrganizerModel,
    RespondType.SEND,
    null
)

val DemoReceivedRespondsModel = RespondModel(
    1,
    DemoMeetingModel,
    "Классно выглядишь, пойдем? Я вроде адекватный))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    listOf(DemoHiddenPhotoModel, DemoHiddenPhotoModel, DemoHiddenPhotoModel, DemoHiddenPhotoModel)
)