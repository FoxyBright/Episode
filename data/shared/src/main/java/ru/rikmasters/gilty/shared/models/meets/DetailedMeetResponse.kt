package ru.rikmasters.gilty.shared.models.meets

import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime
import ru.rikmasters.gilty.shared.common.extentions.durationToString
import ru.rikmasters.gilty.shared.model.enumeration.*
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.models.*

data class DetailedMeetResponse(
    val id: String,
    val userId: String,
    val organizer: User,
    val category: Category,
    val type: String,
    val condition: String,
    val isOnline: Boolean,
    val tags: List<Tag>,
    val description: String? = null,
    val datetime: String,
    val duration: Int,
    val isPrivate: Boolean,
    val withoutResponds: Boolean,
    val status: String,
    val membersMax: Int,
    val membersCount: Int,
    val members: List<User>,
    val requirements: List<DetailedRequirement>? = null,
    val hideAddress: Boolean? = null,
    val location: Location? = null,
    val place: String? = null,
    val address: String? = null,
    val memberState: String,
    val price: Int? = null,
) {
    
    fun map() = FullMeetingModel(
        id = id,
        userId = userId,
        organizer = organizer.map(),
        category = category.map(),
        type = MeetType.valueOf(type),
        condition = ConditionType.valueOf(condition),
        isOnline = isOnline,
        tags = tags.map { it.map() },
        description = description ?: "",
        datetime = LocalDateTime.of(datetime).toString(),
        duration = durationToString(duration),
        isPrivate = isPrivate,
        withoutResponds = withoutResponds,
        status = MeetStatusType.valueOf(status),
        membersMax = membersMax,
        membersCount = membersCount,
        members = members.map { it.map() },
        requirements = requirements?.let { req -> req.map { it.map() } },
        hideAddress = hideAddress,
        location = location?.map(),
        place = place,
        address = address,
        memberState = MemberStateType.valueOf(memberState),
        price = price
    )
}