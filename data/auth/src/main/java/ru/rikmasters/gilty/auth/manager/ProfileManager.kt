package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.profile.Image
import ru.rikmasters.gilty.auth.profile.ProfileWebSource

class ProfileManager(
    
    private val profileWebSource: ProfileWebSource

) {
    
    suspend fun getProfile() =
        profileWebSource.getUserData()
    
    suspend fun getProfileHiddens() =
        profileWebSource.getProfileHiddens()
    
    suspend fun deleteHidden(image: Image) {
        profileWebSource.deleteHidden(image)
    }
    
}