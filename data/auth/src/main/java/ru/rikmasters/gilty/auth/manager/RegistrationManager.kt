package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.meetings.MeetingWebSource
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import java.io.File

class RegistrationManager(
    
    private val profileWebSource: ProfileWebSource,
    
    private val meetWebSource: MeetingWebSource

) {
    
    suspend fun getCategoriesList(): List<CategoryModel> =
        meetWebSource.getCategoriesList()
    
    suspend fun isUserRegistered(): Pair<Boolean, String?> =
        profileWebSource.isUserRegistered()
    
    suspend fun setAvatar(file: File, points: List<Int>) {
        profileWebSource.setUserAvatar(
            file,
            points[0],
            points[1],
            points[3],
            points[2],
        )
    }
    
    suspend fun setHidden(files: List<File>) {
        profileWebSource.setHidden(files)
    }
    
    suspend fun isNameOccupied(name: String): Boolean =
        profileWebSource.checkUserName(name)
    
    
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