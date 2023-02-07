package ru.rikmasters.gilty.shared.models.request.meets

import ru.rikmasters.gilty.shared.model.meeting.LocationModel

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

data class Requirement(
    
    val gender: String?,
    
    val ageMin: Int?,
    
    val ageMax: Int?,
    
    val orientationId: String?,
)

data class Location(
    
    val hide: Boolean? = null,
    
    val lat: Int? = null,
    
    val lng: Int? = null,
    
    val place: String? = null,
    
    val address: String? = null,
) {
    
    fun map() = LocationModel(
        hide, lat, lng, place, address
    )
}