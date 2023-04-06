package ru.rikmasters.gilty.meetings.addmeet

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.*
import java.util.UUID.randomUUID

data class AddMeet(
    val id: String = randomUUID().toString(),
    val category: CategoryModel? = null,
    val type: MeetType,
    val isOnline: Boolean,
    val condition: ConditionType,
    val price: String,
    val photoAccess: Boolean,
    val chatForbidden: Boolean,
    val tags: List<TagModel>,
    val description: String,
    val dateTime: String,
    val duration: String,
    val hide: Boolean,
    val lat: Double,
    val lng: Double,
    val place: String,
    val address: String,
    val isPrivate: Boolean,
    val memberCount: String,
    val requirementsType: String,
    val requirements: List<RequirementModel>,
    val withoutResponds: Boolean,
    val memberLimited: Boolean,
): DomainEntity {
    
    constructor(): this(
        category = CategoryModel(),
        type = MeetType.NON_SELECT,
        isOnline = false,
        condition = ConditionType.NON_SELECT,
        price = "",
        photoAccess = false,
        chatForbidden = false,
        tags = emptyList(),
        description = "",
        dateTime = "",
        duration = "",
        hide = false,
        lat = 0.0,
        lng = 0.0,
        place = "",
        address = "",
        isPrivate = false,
        memberCount = "",
        requirementsType = "",
        requirements = emptyList(),
        withoutResponds = false,
        memberLimited = false
    )
    
    fun map() = AddMeetModel(
        category, type, isOnline, condition, price,
        photoAccess, chatForbidden, tags, description,
        dateTime, duration, hide, lat, lng,
        place, address, isPrivate, memberCount,
        requirementsType, requirements, withoutResponds,
        memberLimited
    )
    
    override fun primaryKey() = id
}
