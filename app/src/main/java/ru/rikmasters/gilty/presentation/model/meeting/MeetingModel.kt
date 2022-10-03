package ru.rikmasters.gilty.presentation.model.meeting

import ru.rikmasters.gilty.presentation.model.enumeration.ConditionType
import ru.rikmasters.gilty.presentation.model.enumeration.MeetType
import java.util.*

sealed interface MeetingModel{

    val id: UUID

    val title: String

    val condition: ConditionType

    val category: CategoryModel

    val dateTime: String

    val organizer: OrganizerModel

    val isOnline: Boolean
}

data class ShortMeetingModel(

    override val id: UUID,

    override val title: String,

    override val condition: ConditionType,

    override val category: CategoryModel,

    override val dateTime: String,

    override val organizer: OrganizerModel,

    override val isOnline: Boolean

): MeetingModel

data class FullMeetingModel(

    override val id: UUID,

    override val title: String,

    override val condition: ConditionType,

    override val category: CategoryModel,

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
    category = DemoCategoryModel,
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
    category = DemoCategoryModel,
    dateTime = "2022-09-16T08:35:54.140Z",
    organizer = DemoOrganizerModel,
    true
)

val DemoMeetingList = listOf(DemoMeetingModel, DemoMeetingModel, DemoMeetingModel, DemoMeetingModel, DemoMeetingModel)
