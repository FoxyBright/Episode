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

data class FullMeetingModel( // TODO заменить VAR на VAL сделано для верстки
    
    override var id: UUID,
    
    override var title: String,
    
    override var condition: ConditionType,
    
    override var category: ShortCategoryModel,
    
    override var duration: String,
    
    var type: MeetType,
    
    override var dateTime: String,
    
    override var organizer: OrganizerModel,
    
    override var isOnline: Boolean,
    
    var tags: List<TagModel>,
    
    var description: String,
    
    var isPrivate: Boolean,
    
    var memberCount: Int,
    
    var requirements: List<MeetingRequirementModel>,
    
    var place: String,
    
    var address: String,
    
    var hideAddress: Boolean,
    
    var price: Int? = null

): MeetingModel

enum class MeetStatus { ACTIVE, INACTIVE }

val DemoFullMeetingModel = FullMeetingModel(
    id = UUID.randomUUID(),
    title = "Поход в кино",
    condition = ConditionType.MEMBER_PAY,
    category = DemoShortCategoryModel,
    duration = "2 часа",
    type = MeetType.GROUP,
    dateTime = "2022-11-28T20:00:54.140Z",
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
