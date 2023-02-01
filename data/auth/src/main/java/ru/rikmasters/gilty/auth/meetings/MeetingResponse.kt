package ru.rikmasters.gilty.auth.meetings

import ru.rikmasters.gilty.auth.profile.ProfileResponse
import ru.rikmasters.gilty.data.ktor.Ktor.anyLog
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.common.extentions.durationToString
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.DemoRequirementModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

data class Tag(
    val id: String? = null,
    val title: String? = null,
) {
    
    fun map(): TagModel = TagModel(
        id.toString(), title.toString()
    )
}

data class MeetingResponse(
    val id: String,
    val status: String? = null,
    val type: String? = null,
    val tags: List<Tag>? = null,
    val condition: String? = null,
    val category: Category,
    val datetime: String? = null,
    val duration: Int? = null,
    val organizer: ProfileResponse? = null,
    val isOnline: Boolean? = null,
    val isAnonymous: Boolean? = null,
    val memberState: String? = null,
) {
    
    fun map(): MeetingModel = MeetingModel(
        id, tags?.first()?.title.toString(),
        ConditionType.valueOf(condition.toString()),
        category.map(),
        durationToString(duration ?: 0),
        MeetType.valueOf(type.toString()),
        datetime?.let { LocalDateTime.of(it).toString() }.toString(),
        organizer?.map()?.mapToOrganizerModel(),
        anyLog(isOnline) == true,
        tags?.map { it.map() } ?: listOf(),
        "", isAnonymous == true, 0,
        DemoRequirementModel, "", "", false
    )
}
