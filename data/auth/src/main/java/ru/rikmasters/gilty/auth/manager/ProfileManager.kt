package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.profile.ProfileWebSource

class ProfileManager(
    
    private val profileWebSource: ProfileWebSource

) {
    
    suspend fun getProfile() =
        profileWebSource.getUserData()
    
    
}