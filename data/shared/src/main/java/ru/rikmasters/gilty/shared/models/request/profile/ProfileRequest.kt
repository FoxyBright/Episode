package ru.rikmasters.gilty.shared.models.request.profile

data class ProfileRequest(
    
    val username: String? = null,
    
    val gender: String? = null,
    
    val age: Int? = null,
    
    val orientationId: String? = null,
    
    val aboutMe: String? = null

)