package ru.rikmasters.gilty.auth.meetings

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.common.extentions.durationToString
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

data class MainMeetResponse(
    val id: String,
    val status: String,
    val type: String,
    val tags: List<TagModel>,
    val condition: String,
    val category: Category,
    val datetime: String,
    val duration: Int,
    val organizer: Organizer,
    val isOnline: Boolean,
    val memberState: String,
) {
    
    fun map() = MeetingModel(
        id = id,
        title = tags.joinToString(separator = ", ") { it.title },
        condition = ConditionType.valueOf(condition),
        category = category.map(),
        durationToString(duration),
        MeetType.valueOf(type),
        datetime = "${of(datetime)}",
        organizer = organizer.map(),
        isOnline = isOnline,
        tags = tags, "",
        isPrivate = false,
        memberCount = 0,
        requirements = null,
        place = "",
        address = "",
        hideAddress = false,
        price = null
    )
}