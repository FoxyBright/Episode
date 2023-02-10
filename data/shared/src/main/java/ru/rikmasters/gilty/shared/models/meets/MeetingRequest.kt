package ru.rikmasters.gilty.shared.models.meets

import ru.rikmasters.gilty.shared.models.Location
import ru.rikmasters.gilty.shared.models.Requirement

data class MeetingRequest(
    val categoryId: String?,
    val type: String?,
    val isOnline: Boolean?,
    val condition: String?,
    val price: Int?,
    val photoAccess: Boolean?,
    val chatForbidden: Boolean?,
    val tags: List<String>?,
    val description: String?,
    val datetime: String?,
    val duration: Int?,
    val location: Location?,
    val isPrivate: Boolean?,
    val membersCount: Int?,
    val requirementsType: String?,
    val requirements: List<Requirement>?,
    val withoutResponds: Boolean?,
)