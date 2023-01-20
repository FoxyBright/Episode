package ru.rikmasters.gilty.auth.meetings

import ru.rikmasters.gilty.auth.profile.ProfileResponse
import ru.rikmasters.gilty.data.ktor.Ktor.anyLog
import ru.rikmasters.gilty.shared.model.enumeration.getCategoriesType
import ru.rikmasters.gilty.shared.model.enumeration.getConditionType
import ru.rikmasters.gilty.shared.model.enumeration.getMeetType
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingRequirementModel
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

data class Category(
    val id: String,
    val name: String? = null,
    val color: String? = null,
    val icon_type: String? = null,
)

data class MeetingResponse(
    val id: String,
    val status: String? = null,
    val type: String? = null,
    val tags: List<Tag>? = null,
    val condition: String? = null,
    val category: Category? = null,
    val datetime: String? = null,
    val duration: Int? = null,
    val organizer: ProfileResponse? = null,
    val isOnline: Boolean? = null,
    val isAnonymous: Boolean? = null,
    val memberState: String? = null,
) {
    
    fun map(): MeetingModel = MeetingModel(
        id, tags?.first()?.title.toString(),
        getConditionType(condition.toString()),
        getCategoriesType(category?.icon_type.toString()),
        duration.toString(), getMeetType(type.toString()),
        datetime.toString(),
        organizer?.mapToProfileModel()?.mapToOrganizerModel(),
        anyLog(isOnline) == true,
        tags?.map { it.map() } ?: listOf(),
        "", isAnonymous == true, 0,
        DemoMeetingRequirementModel, "", "", false
    )
}
