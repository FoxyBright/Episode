package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.*
import java.util.UUID

data class MeetWithRespondsModel(
    val id: String,
    val tags: List<TagModel>,
    val is_online: Boolean,
    val organizer: UserModel,
    val responds_count: Int,
    val responds: List<RespondModel>,
) {
    
    constructor(list: List<RespondModel>): this(
        UUID.randomUUID().toString(),
        listOf(TagModel()), (false),
        UserModel(), list.size, list
    )
}

val DemoMeetWithRespondsModel = MeetWithRespondsModel(
    UUID.randomUUID().toString(),
    DemoTagList,
    true,
    DemoUserModel,
    3,
    listOf(
        DemoRespondModel,
        DemoRespondModel.copy(
            photoAccess = false
        )
    )
)