package ru.rikmasters.gilty.shared.model.notification

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.UUID

data class MeetWithRespondsModel(
    val id: String,
    val tags: List<TagModel>,
    val is_online: Boolean,
    val organizer: UserModel,
    val responds_count: Int,
    val responds: List<RespondModel>,
) {
    
    fun map(
        photoPaging: (String) -> Flow<PagingData<AvatarModel>>,
    ) = MeetWithRespondsModelWithPhotos(
        id = id,
        tags = tags,
        is_online = is_online,
        organizer = organizer,
        responds_count = responds_count,
        responds = responds.map {
            if(it.photoAccess)
                RespondWithPhotos(
                    id = it.id,
                    author = it.author,
                    comment = it.comment,
                    photoAccess = true,
                    photos = photoPaging(it.albumId)
                )
            else RespondWithPhotos(
                id = it.id,
                author = it.author,
                comment = it.comment,
                photoAccess = false,
                photos = null
            )
        }
    )
}

data class MeetWithRespondsModelWithPhotos(
    val id: String,
    val tags: List<TagModel>,
    val is_online: Boolean,
    val organizer: UserModel,
    val responds_count: Int,
    val responds: List<RespondWithPhotos>,
)

@Suppress("unused")
val DemoMeetWithRespondsModel = MeetWithRespondsModel(
    UUID.randomUUID().toString(),
    DemoTagList,
    (true),
    DemoUserModel,
    (3),
    listOf(
        DemoRespondModel,
        DemoRespondModel.copy(
            photoAccess = false
        )
    )
)

/*

val DemoMeetWithRespondsModelWithPhotos = MeetWithRespondsModelWithPhotos(
    UUID.randomUUID().toString(),
    DemoTagList,
    (true),
    DemoUserModel,
    (3),
    listOf(
        DemoRespondModelWithPhoto,
        DemoRespondModelWithPhoto
    )
)
 */
