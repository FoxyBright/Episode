package ru.rikmasters.gilty.shared.model.meeting

import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import java.util.UUID

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
    val memberCount: String,
    val requirementsType: String,
    val requirements: List<RequirementModel>,
    val withoutResponds: Boolean,
    val memberLimited: Boolean,
) {
    
    fun map(profile: UserModel) = MeetingModel(
        id = UUID.randomUUID().toString(),
        title = tags.joinToString(", ")
        { t -> t.title },
        condition = condition,
        category = category!!,
        duration = duration,
        type = type,
        datetime = dateTime,
        organizer = profile,
        isOnline = isOnline,
        tags = tags,
        description = description.ifEmpty { null },
        isPrivate = isPrivate,
        memberCount = try {
            memberCount.toInt()
        } catch(e: Exception) {
            1
        }, requirements = null,
        place = place,
        address = address,
        hideAddress = hide,
        price = try {
            price.toInt()
        } catch(e: Exception) {
            0
        }
    )
}
