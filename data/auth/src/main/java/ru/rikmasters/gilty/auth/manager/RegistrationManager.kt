package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.profile.ProfileWebSource
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.GenderType
import java.io.File

class RegistrationManager(
    
    private val profileWebSource: ProfileWebSource

) {
    
    suspend fun isUserRegistered(): Boolean =
        profileWebSource.isUserRegistered()
    
    suspend fun setAvatar(file: File) {
        profileWebSource.setUserAvatar(file)
    }
    
    suspend fun setHidden(files: List<File>) {
        profileWebSource.setHidden(files)
    }
    
    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
    ) {
        profileWebSource.setUserData(
            username, aboutMe, age, gender
        )
    }
}