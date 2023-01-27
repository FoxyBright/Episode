package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

data class RespondModel(
    val id: Int,
    val meet: MeetingModel,
    val comment: String?,
    val sender: OrganizerModel,
    val type: RespondType,
    val hiddenPhoto: List<Pair<AvatarModel, Boolean>>?
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

val DemoReceivedRespondModelWithoutPhoto = RespondModel(
    1,
    DemoMeetingModel,
    "Покажи свои фото))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    null
)