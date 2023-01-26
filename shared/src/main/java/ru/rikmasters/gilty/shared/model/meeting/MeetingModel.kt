package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import java.util.UUID

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

enum class MeetStatus { ACTIVE, INACTIVE }

fun getDemoMeetingModel(
    id: String = UUID.randomUUID().toString(),
    title: String = "Поход в кино",
    condition: ConditionType = ConditionType.MEMBER_PAY,
    category: CategoryModel = DemoCategoryModel,
    duration: String = "2 часа",
    type: MeetType = MeetType.GROUP,
    dateTime: String = "2022-11-28T20:00:54.140Z",
    organizer: OrganizerModel = DemoOrganizerModel,
    isOnline: Boolean = false,
    tags: List<TagModel> = DemoTagList,
    description: String = "Описание вечеринки",
    isPrivate: Boolean = false,
    memberCount: Int = 4,
    requirements: RequirementModel =
        DemoRequirementModel,
    place: String = "Москва-сити",
    address: String = "Москва, ул. Пушкина 42",
    hideAddress: Boolean = false,
    price: Int? = 600,
) = MeetingModel(
    id, title, condition,
    category, duration, type,
    dateTime, organizer, isOnline,
    tags, description, isPrivate,
    memberCount, requirements,
    place, address, hideAddress, price
)

val DemoMeetingModel =
    getDemoMeetingModel()

val DemoMeetingList = listOf(
    getDemoMeetingModel(),
    getDemoMeetingModel(),
    getDemoMeetingModel(),
    getDemoMeetingModel(),
    getDemoMeetingModel()
)
