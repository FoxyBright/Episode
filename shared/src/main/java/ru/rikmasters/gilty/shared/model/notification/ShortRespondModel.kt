package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

data class RespondsFeedback(
    val id: String,
    val tags: List<TagModel>,
    val isOnline: Boolean,
    val organizerModel: OrganizerModel,
    val respondsCount: Int,
    val responds: List<RespondModel>,
)

data class RespondModel(
    val id: String,
    val author: OrganizerModel,
    val comment: String,
    val photoAccess: Boolean,
)

data class ShortRespondModel(
    val id: Int,
    val meet: MeetingModel,
    val comment: String?,
    val sender: OrganizerModel,
    val type: RespondType,
    val hiddenPhoto: List<Pair<AvatarModel, Boolean>>?,
)

val DemoSendRespondsModel = ShortRespondModel(
    1,
    DemoMeetingModel,
    null,
    DemoOrganizerModel,
    RespondType.SEND,
    null
)

val DemoReceivedRespondsModel = ShortRespondModel(
    1, DemoMeetingModel,
    "Классно выглядишь, пойдем? Я вроде адекватный))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    listOf(
        Pair(DemoAvatarModel, true),
        Pair(DemoAvatarModel, true),
        Pair(DemoAvatarModel, false),
        Pair(DemoAvatarModel, false)
    )
)

val DemoReceivedShortRespondModelWithoutPhoto = ShortRespondModel(
    1,
    DemoMeetingModel,
    "Покажи свои фото))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    null
)