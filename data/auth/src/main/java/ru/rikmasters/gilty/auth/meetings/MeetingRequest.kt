package ru.rikmasters.gilty.auth.meetings

data class MeetingRequest(
    
    val categoryId: String,
    
    val type: String,
    
    val isOnline: Boolean,
    
    val condition: String,
    
    val price: Int,
    
    val photoAccess: Boolean,
    
    val chatForbidden: Boolean,
    
    val tags: List<String>,
    
    val description: String,
    
    val dateTime: String,
    
    val duration: Int,
    
    val location: Location,
    
    val isPrivate: Boolean,
    
    val memberCount: Int,
    
    val requirementsType: String,
    
    val requirements: List<Requirement>,
    
    val withoutResponds: Boolean,
)

data class Requirement(
    
    val gender: String,
    
    val ageMin: Int,
    
    val ageMax: Int,
    
    val orientationId: String,
)

data class Location(
    
    val hide: Boolean,
    
    val lat: Int,
    
    val lng: Int,
    
    val place: String,
    
    val address: String,
)