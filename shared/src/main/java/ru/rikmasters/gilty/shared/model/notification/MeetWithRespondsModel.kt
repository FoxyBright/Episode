package ru.rikmasters.gilty.shared.model.notification

import ru.rikmasters.gilty.shared.model.meeting.*
import java.util.UUID

data class MeetWithRespondsModel(
    val id: String,
    val tags: List<TagModel>,
    val is_online: Boolean,
    val organizer: OrganizerModel,
    val responds_count: Int,
    val responds: List<RespondModel>,
)

val DemoMeetWithRespondsModel = MeetWithRespondsModel(
    UUID.randomUUID().toString(),
    DemoTagList,
    true,
    DemoOrganizerModel,
    3,
    listOf(
        DemoRespondModel,
        DemoRespondModel.copy(
            photoAccess = false
        )
    )
)