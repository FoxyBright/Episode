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

val DemoRespondModel = RespondModel(
    UUID.randomUUID().toString(),
    DemoUserModel,
    "comment",
    "album",
    true
)