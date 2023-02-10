package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import java.util.UUID

data class RespondModel(
    val id: String,
    val author: UserModel,
    val comment: String,
    val albumId: String,
    val photoAccess: Boolean,
)

//
//
//data class ShortRespondModel(
//    val id: String,
//    val meet: MeetingModel,
//    val comment: String?,
//    val sender: OrganizerModel,
//    val type: RespondType,
//    val hiddenPhoto: List<Pair<AvatarModel, Boolean>>?,
//)
//
//val DemoSendRespondsModel = ShortRespondModel(
//    UUID.randomUUID().toString(),
//    DemoMeetingModel,
//    null,
//    DemoOrganizerModel,
//    RespondType.SENT,
//    null
//)
//
//val DemoReceivedRespondsModel = ShortRespondModel(
//    UUID.randomUUID().toString(), DemoMeetingModel,
//    "Классно выглядишь, пойдем? Я вроде адекватный))",
//    DemoOrganizerModel,
//    RespondType.RECEIVED,
//    listOf(
//        Pair(DemoAvatarModel, true),
//        Pair(DemoAvatarModel, true),
//        Pair(DemoAvatarModel, false),
//        Pair(DemoAvatarModel, false)
//    )
//)
//
//val DemoReceivedShortRespondModelWithoutPhoto = ShortRespondModel(
//    UUID.randomUUID().toString(),
//    DemoMeetingModel,
//    "Покажи свои фото))",
//    DemoOrganizerModel,
//    RespondType.RECEIVED,
//    null
//)
//
//val DemoRespondModelList = listOf(
//    DemoSendRespondsModel,
//    DemoReceivedShortRespondModelWithoutPhoto
//)

val DemoRespondModel = RespondModel(
    UUID.randomUUID().toString(),
    DemoUserModel,
    "comment",
    "album",
    true
)

val DemoRespondModelList = listOf(
    DemoRespondModel,
    DemoRespondModel,
    DemoRespondModel,
)
