package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import java.util.UUID

sealed interface MeetingModel{

    val id: UUID

    val title: String

    val condition: ConditionType

    val category: ShortCategoryModel

    val dateTime: String

    val organizer: OrganizerModel

    val isOnline: Boolean
}

data class ShortMeetingModel(

    override val id: UUID,

    override val title: String,

    override val condition: ConditionType,

    override val category: ShortCategoryModel,

    override val dateTime: String,

    override val organizer: OrganizerModel,

    override val isOnline: Boolean

): MeetingModel

data class FullMeetingModel(

    override val id: UUID,

    override val title: String,

    override val condition: ConditionType,

    override val category: ShortCategoryModel,

    val type: MeetType,

    override val dateTime: String,

    override val organizer: OrganizerModel,

    override val isOnline: Boolean,

    val tags: List<TagModel>,

    val description: String,

    val isPrivate: Boolean,

    val memberCount: Int,

    val requirements: List<MeetingRequirementModel>,

    val place: String,

    val address: String,

    val hideAddress: Boolean

): MeetingModel

val DemoFullMeetingModel = FullMeetingModel(
    id = UUID.randomUUID(),
    title = "Поход в кино",
    condition = ConditionType.FREE,
    category = DemoShortCategoryModel,
    type = MeetType.GROUP,
    dateTime = "2022-09-16T08:35:54.140Z",
    organizer = DemoOrganizerModel,
    isOnline = true,
    tags = DemoTagList,
    description = "Описание вечеринки",
    isPrivate = false,
    memberCount = 4,
    requirements = ListDemoMeetingRequirementModel,
    place = "Москва-сити",
    address = "Москва, ул. Пушкина 42",
    hideAddress = false
)

val DemoMeetingModel = ShortMeetingModel(
    id = UUID.randomUUID(),
    title = "R1, клубы, вечеринки",
    condition = ConditionType.MEMBER_PAY,
    category = DemoShortCategoryModel,
    dateTime = "2022-09-16T08:35:54.140Z",
    organizer = DemoOrganizerModel,
    true
)

val DemoMeetingList = listOf(DemoMeetingModel, DemoMeetingModel, DemoMeetingModel, DemoMeetingModel, DemoMeetingModel)