package ru.rikmasters.gilty.shared.model.notification

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.rikmasters.gilty.shared.model.meeting.DemoUserModel
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.UUID

data class RespondModel(
    val id: String,
    val author: UserModel,
    val comment: String,
    val albumId: String,
    val photoAccess: Boolean
) {

    fun map(
        photos: (String) -> Flow<PagingData<AvatarModel>>
    ) = RespondWithPhotos(
        id,
        author,
        comment,
        if (photoAccess) {
            photos(albumId)
        } else null,
        photoAccess
    )
}

data class RespondWithPhotos(
    val id: String,
    val author: UserModel,
    val comment: String,
    val photos: Flow<PagingData<AvatarModel>>?,
    val photoAccess: Boolean
)

val DemoRespondModel = RespondModel(
    UUID.randomUUID().toString(),
    DemoUserModel,
    "comment",
    "album",
    true
)

/*
val DemoRespondModelWithPhoto = RespondWithPhotos(
    UUID.randomUUID().toString(),
    DemoUserModel,
    "comment",
    listOf(
        DemoAvatarModel,
        DemoAvatarModel,
        DemoAvatarModel
    ),
    true
)

 */
