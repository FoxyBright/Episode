package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import java.util.UUID

sealed interface MeetingModel {

    val id: UUID

    val title: String

    val condition: ConditionType

    val category: ShortCategoryModel

    val dateTime: String

    val duration: String

    val organizer: OrganizerModel

    val isOnline: Boolean
}

data class FullMeetingModel(

    override val id: UUID,

    override val title: String,

    override val condition: ConditionType,

    override val category: ShortCategoryModel,

    override val duration: String,

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

    val hideAddress: Boolean,

    val price: Int? = null

) : MeetingModel

val DemoFullMeetingModel = FullMeetingModel(
    id = UUID.randomUUID(),
    title = "Поход в кино",
    condition = ConditionType.FREE,
    category = DemoShortCategoryModel,
    duration = "2 часа",
    type = MeetType.GROUP,
    dateTime = "2022-11-28T08:35:54.140Z",
    organizer = DemoOrganizerModel,
    isOnline = false,
    tags = DemoTagList,
    description = "Описание вечеринки",
    isPrivate = false,
    memberCount = 4,
    requirements = ListDemoMeetingRequirementModel,
    place = "Москва-сити",
    address = "Москва, ул. Пушкина 42",
    hideAddress = false,
    price = 600
)

val DemoMeetingList = listOf(
    DemoFullMeetingModel,
    DemoFullMeetingModel,
    DemoFullMeetingModel,
    DemoFullMeetingModel,
    DemoFullMeetingModel
)
