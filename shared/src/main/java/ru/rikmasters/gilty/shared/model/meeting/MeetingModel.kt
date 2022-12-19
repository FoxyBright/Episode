package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import java.util.UUID

data class MeetingModel( // TODO заменить VAR на VAL сделано для верстки
    var id: UUID,
    var title: String,
    var condition: ConditionType,
    var category: CategoriesType,
    var duration: String,
    var type: MeetType,
    var dateTime: String,
    var organizer: OrganizerModel,
    var isOnline: Boolean,
    var tags: List<TagModel>,
    var description: String,
    var isPrivate: Boolean,
    var memberCount: Int,
    var requirements: List<MeetingRequirementModel>,
    var place: String,
    var address: String,
    var hideAddress: Boolean,
    var price: Int? = null
)

enum class MeetStatus { ACTIVE, INACTIVE }

fun getDemoMeetingModel(
    id: UUID = UUID.randomUUID(),
    title: String = "Поход в кино",
    condition: ConditionType = ConditionType.MEMBER_PAY,
    category: CategoriesType = CategoriesType.ENTERTAINMENT,
    duration: String = "2 часа",
    type: MeetType = MeetType.GROUP,
    dateTime: String = "2022-11-28T20:00:54.140Z",
    organizer: OrganizerModel = DemoOrganizerModel,
    isOnline: Boolean = false,
    tags: List<TagModel> = DemoTagList,
    description: String = "Описание вечеринки",
    isPrivate: Boolean = false,
    memberCount: Int = 4,
    requirements: List<MeetingRequirementModel> =
        ListDemoMeetingRequirementModel,
    place: String = "Москва-сити",
    address: String = "Москва, ул. Пушкина 42",
    hideAddress: Boolean = false,
    price: Int? = 600
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
