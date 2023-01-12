package ru.rikmasters.gilty.auth.profile

data class ProfileRequest(
    
    val username: String? = null,
    
    val gender: String? = null,
    
    val age: Int? = null,
    
    val orientationId: String? = null,
    
    val aboutMe: String? = null

)