package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.*
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType.ACTIVE
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_MEMBER
import java.util.UUID.randomUUID

data class MeetingModel(
    val id: String,
    val title: String,
    val condition: ConditionType,
    val category: CategoryModel,
    val duration: String,
    val type: MeetType,
    val datetime: String,
    val organizer: OrganizerModel?,
    val isOnline: Boolean,
    val tags: List<TagModel>,
    val description: String,
    val isPrivate: Boolean,
    val memberCount: Int,
    val requirements: RequirementModel?,
    val place: String,
    val address: String,
    val hideAddress: Boolean,
    val price: Int? = null,
    val memberState: MemberStateType? = null,
) {
    
    fun map() = FullMeetingModel(
        id = id,
        userId = id,
        condition = condition,
        category = category,
        duration = duration,
        type = type,
        datetime = datetime,
        organizer = organizer ?: DemoOrganizerModel,
        isOnline = isOnline,
        tags = tags,
        description = description,
        isPrivate = isPrivate,
        requirements = requirements?.let { listOf(it) } ?: emptyList(),
        place = place,
        membersCount = memberCount,
        address = address,
        hideAddress = hideAddress,
        price = price,
        withoutResponds = false,
        status = ACTIVE,
        membersMax = 0,
        members = emptyList(),
        location = null,
        memberState = memberState
    )
}

data class FullMeetingModel(
    val id: String,
    val userId: String,
    val organizer: OrganizerModel,
    val category: CategoryModel,
    val type: MeetType,
    val condition: ConditionType,
    val isOnline: Boolean,
    val tags: List<TagModel>,
    val description: String,
    val datetime: String,
    val duration: String,
    val isPrivate: Boolean,
    val withoutResponds: Boolean,
    val status: MeetStatusType,
    val membersMax: Int,
    val membersCount: Int,
    val members: List<MemberModel>,
    val requirements: List<RequirementModel>? = null,
    val hideAddress: Boolean? = null,
    val location: LocationModel? = null,
    val place: String? = null,
    val address: String? = null,
    val memberState: MemberStateType?,
    val price: Int? = null,
) {
    
    fun map() = MeetingModel(
        id = id,
        title = tags.joinToString(separator = ", ") { it.title },
        condition = condition,
        category = category,
        duration = duration,
        type = type,
        datetime = datetime,
        organizer = organizer,
        isOnline = isOnline,
        tags = tags,
        description = description,
        isPrivate = isPrivate,
        memberCount = membersCount,
        requirements = requirements?.first(),
        place = place ?: "",
        address = address ?: "",
        hideAddress = hideAddress == true,
        price = price,
        memberState = memberState
    )
}

val DemoFullMeetingModel = FullMeetingModel(
    id = randomUUID().toString(),
    userId = randomUUID().toString(),
    condition = MEMBER_PAY,
    category = DemoCategoryModel,
    duration = "2 часа",
    type = MeetType.GROUP,
    organizer = DemoOrganizerModel,
    isOnline = true,
    tags = DemoTagList,
    datetime = "2022-11-28T20:00:54.140Z",
    description = "Описание вечеринки",
    isPrivate = false,
    requirements = DemoRequirementModelList,
    place = "Москва-сити",
    address = "Москва, ул. Пушкина 42",
    hideAddress = false,
    withoutResponds = false,
    status = ACTIVE,
    membersCount = DemoMemberModelList.size,
    membersMax = 10,
    members = DemoMemberModelList,
    location = DemoLocationModel,
    memberState = IS_MEMBER
)

val DemoMeetingModel = MeetingModel(
    id = randomUUID().toString(),
    title = "Поход в кино",
    condition = MEMBER_PAY,
    category = DemoCategoryModel,
    duration = "2 часа",
    type = MeetType.GROUP,
    datetime = "2022-11-28T20:00:54.140Z",
    organizer = DemoOrganizerModel,
    isOnline = false,
    tags = DemoTagList,
    description = "Описание вечеринки",
    isPrivate = false,
    memberCount = 4,
    requirements = DemoRequirementModel,
    place = "Москва-сити",
    address = "Москва, ул. Пушкина 42",
    hideAddress = false,
    price = 600,
    memberState = IS_MEMBER
)

val DemoMeetingList = listOf(
    DemoMeetingModel,
    DemoMeetingModel,
    DemoMeetingModel,
    DemoMeetingModel,
    DemoMeetingModel
)
