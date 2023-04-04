package ru.rikmasters.gilty.shared.model.notification

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
    
    suspend fun map(
        returnPhotoList: suspend (String) -> List<AvatarModel>,
    ): MeetWithRespondsModelWithPhotos {
        return MeetWithRespondsModelWithPhotos(
            id, tags, is_online, organizer, responds_count,
            responds.map { it.map(returnPhotoList(it.albumId)) }
        )
    }
}

data class MeetWithRespondsModelWithPhotos(
    val id: String,
    val tags: List<TagModel>,
    val is_online: Boolean,
    val organizer: UserModel,
    val responds_count: Int,
    val responds: List<RespondWithPhotos>,
) {
    
    constructor(list: List<RespondModel>): this(
        UUID.randomUUID().toString(),
        listOf(TagModel()), (false),
        UserModel(), list.size, list.map { it.map(emptyList()) }
    )
}

val DemoMeetWithRespondsModel = MeetWithRespondsModel(
    UUID.randomUUID().toString(),
    DemoTagList, (true),
    DemoUserModel, (3), listOf(
        DemoRespondModel,
        DemoRespondModel.copy(
            photoAccess = false
        )
    )
)

val DemoMeetWithRespondsModelWithPhotos = MeetWithRespondsModelWithPhotos(
    UUID.randomUUID().toString(),
    DemoTagList, (true), DemoUserModel, (3), listOf(
        DemoRespondModelWithPhoto,
        DemoRespondModelWithPhoto,
    )
)