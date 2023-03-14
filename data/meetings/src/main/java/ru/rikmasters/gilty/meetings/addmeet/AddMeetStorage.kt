package ru.rikmasters.gilty.meetings.addmeet

import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.core.data.repository.Repository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

class AddMeetStorage(
    override val primarySource: DbSource,
): Repository<DbSource>(primarySource) {
    
    private fun List<AddMeet>.map() =
        this.firstOrNull()?.map()
    
    val addMeetFlow = primarySource
        .listenAll(AddMeet::class)
        .map { it.map() }
    
    suspend fun update(
        category: CategoryModel? = null,
        type: MeetType? = null,
        isOnline: Boolean? = null,
        condition: ConditionType? = null,
        price: String? = null,
        photoAccess: Boolean? = null,
        chatForbidden: Boolean? = null,
        tags: List<TagModel>? = null,
        description: String? = null,
        dateTime: String? = null,
        duration: String? = null,
        hide: Boolean? = null,
        lat: Double? = null,
        lng: Double? = null,
        place: String? = null,
        address: String? = null,
        isPrivate: Boolean? = null,
        memberCount: Int? = null,
        requirementsType: String? = null,
        requirements: List<RequirementModel>? = null,
        withoutResponds: Boolean? = null,
    ) {
        getAddMeet().let {
            primarySource.save(
                it.copy(
                    category = category ?: it.category,
                    type = type ?: it.type,
                    isOnline = isOnline ?: it.isOnline,
                    condition = condition ?: it.condition,
                    price = price ?: it.price,
                    photoAccess = photoAccess ?: it.photoAccess,
                    chatForbidden = chatForbidden ?: it.chatForbidden,
                    tags = tags ?: it.tags,
                    description = description ?: it.description,
                    dateTime = dateTime ?: it.dateTime,
                    duration = duration ?: it.duration,
                    hide = hide ?: it.hide,
                    lat = lat ?: it.lat,
                    lng = lng ?: it.lng,
                    place = place ?: it.place,
                    address = address ?: it.address,
                    isPrivate = isPrivate ?: it.isPrivate,
                    memberCount = memberCount ?: it.memberCount,
                    requirementsType = requirementsType ?: it.requirementsType,
                    requirements = requirements ?: it.requirements,
                    withoutResponds = withoutResponds ?: it.withoutResponds,
                )
            )
        }
    }
    
    private suspend fun getAddMeet() = primarySource
        .findAll(AddMeet::class)
        .firstOrNull() ?: AddMeet()
    
    suspend fun clear() {
        primarySource.deleteAll(AddMeet::class)
    }
}