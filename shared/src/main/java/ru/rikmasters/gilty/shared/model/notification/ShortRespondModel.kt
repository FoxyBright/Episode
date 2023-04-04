package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import java.util.UUID

data class RespondModel(
    val id: String,
    val author: UserModel,
    val comment: String,
    val albumId: String,
    val photoAccess: Boolean,
) {
    
    fun map(
        photoList: List<AvatarModel>,
    ) = RespondWithPhotos(
        id, author,
        comment, photoList,
        photoAccess
    )
}

data class RespondWithPhotos(
    val id: String,
    val author: UserModel,
    val comment: String,
    val photos: List<AvatarModel>,
    val photoAccess: Boolean,
)

val DemoRespondModel = RespondModel(
    UUID.randomUUID().toString(),
    DemoUserModel,
    "comment",
    "album",
    true
)

val DemoRespondModelWithPhoto = RespondWithPhotos(
    UUID.randomUUID().toString(),
    DemoUserModel, "comment",
    listOf(
        DemoAvatarModel,
        DemoAvatarModel,
        DemoAvatarModel,
    ), true
)