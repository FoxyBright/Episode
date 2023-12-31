package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModel

data class MeetWithRespondsResponse(
    val id: String,
    val tags: List<TagModel>,
    val is_online: Boolean,
    val organizer: User,
    val responds_count: Int,
    val responds: List<RespondResponse>,
) {
    
    fun map() = MeetWithRespondsModel(
        id, tags, is_online, organizer.map(),
        responds_count, responds.map { it.map() }
    )
}