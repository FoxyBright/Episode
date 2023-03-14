package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType

data class AddMeetModel(
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
    val memberCount: Int,
    val requirementsType: String,
    val requirements: List<RequirementModel>,
    val withoutResponds: Boolean,
)
