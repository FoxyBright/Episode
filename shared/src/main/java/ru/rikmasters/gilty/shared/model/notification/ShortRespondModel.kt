package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import java.util.UUID

data class RespondModel(
    val id: String,
    val author: OrganizerModel,
    val comment: String,
    val albumId: String,
    val photoAccess: Boolean,
)

data class ShortRespondModel(
    val id: String,
    val meet: MeetingModel,
    val comment: String?,
    val sender: OrganizerModel,
    val type: RespondType,
    val hiddenPhoto: List<Pair<AvatarModel, Boolean>>?,
)

val DemoSendRespondsModel = ShortRespondModel(
    UUID.randomUUID().toString(),
    DemoMeetingModel,
    null,
    DemoOrganizerModel,
    RespondType.SEND,
    null
)

val DemoReceivedRespondsModel = ShortRespondModel(
    UUID.randomUUID().toString(), DemoMeetingModel,
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
    UUID.randomUUID().toString(),
    DemoMeetingModel,
    "Покажи свои фото))",
    DemoOrganizerModel,
    RespondType.RECEIVED,
    null
)

val DemoRespondModelList = listOf(
    DemoSendRespondsModel,
    DemoReceivedShortRespondModelWithoutPhoto
)

val DemoRespondModel = RespondModel(
    UUID.randomUUID().toString(),
    DemoOrganizerModel,
    "comment",
    "album",
    true
)