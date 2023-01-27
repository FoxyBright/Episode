package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType.MEMBER_PAY
import ru.rikmasters.gilty.shared.model.enumeration.MeetStatusType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import java.util.UUID.randomUUID

data class MeetingModel(
    val id: String,
    val title: String,
    val condition: ConditionType,
    val category: CategoryModel,
    val duration: String,
    val type: MeetType,
    val dateTime: String,
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
)

data class FullMeetModel(
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
    val memberState: String,
)

val DemoMeetingModel = MeetingModel(
    id = randomUUID().toString(),
    title = "Поход в кино",
    condition = MEMBER_PAY,
    category = DemoCategoryModel,
    duration = "2 часа",
    type = MeetType.GROUP,
    dateTime = "2022-11-28T20:00:54.140Z",
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
)

val DemoMeetingList = listOf(
    DemoMeetingModel,
    DemoMeetingModel,
    DemoMeetingModel,
    DemoMeetingModel,
    DemoMeetingModel
)
