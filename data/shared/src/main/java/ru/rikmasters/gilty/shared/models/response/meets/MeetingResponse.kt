package ru.rikmasters.gilty.shared.models.response.meets

import ru.rikmasters.gilty.data.ktor.Ktor.anyLog
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime.Companion.of
import ru.rikmasters.gilty.shared.common.extentions.durationToString
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType
import ru.rikmasters.gilty.shared.model.meeting.DemoRequirementModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.models.Category
import ru.rikmasters.gilty.shared.models.response.profile.ProfileResponse

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
    val datetime: String,
    val duration: Int? = null,
    val organizer: ProfileResponse? = null,
    val isOnline: Boolean? = null,
    val isAnonymous: Boolean? = null,
    val withoutResponds: Boolean? = null,
    val memberState: String? = null,
) {
    
    fun map(): MeetingModel = MeetingModel(
        id, tags?.first()?.title.toString(),
        ConditionType.valueOf(condition.toString()),
        category.map(),
        durationToString(duration ?: 0),
        MeetType.valueOf(type.toString()),
        datetime = "${of(datetime)}",
        organizer?.map()?.mapToOrganizerModel(),
        anyLog(isOnline) == true,
        tags?.map { it.map() } ?: listOf(),
        "", isAnonymous == true, 0,
        DemoRequirementModel, "", "", false,
        memberState = memberState?.let { MemberStateType.valueOf(it) } ?: MemberStateType.IS_MEMBER
    )
}