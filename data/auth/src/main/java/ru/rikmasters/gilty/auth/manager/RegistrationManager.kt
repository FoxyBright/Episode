package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.meetings.MeetingWebSource
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import java.io.File

class RegistrationManager(
    
    private val profileWebSource: ProfileWebSource,
    
    private val meetWebSource: MeetingWebSource,
    
    private val profileStore: ProfileStore,
) {
    
    suspend fun profileCompleted() =
        profileStore.checkCompletable()
    
    suspend fun userId() =
        profileStore.getProfile(false).id
    
    suspend fun getCategoriesList(): List<CategoryModel> =
        meetWebSource.getCategoriesList()
    
    suspend fun setAvatar(file: File, points: List<Int>) {
        profileWebSource.setUserAvatar(
            file,
            points[0],
            points[1],
            points[3],
            points[2],
        )
    }
    
    suspend fun addHidden(files: List<File>) {
        profileStore.addHidden(files)
    }
    
    suspend fun isNameOccupied(name: String): Boolean =
        profileWebSource.checkUserName(name)
    
    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
    ) {
        profileStore.updateProfile(
            username, aboutMe, age, gender
        )
    }
}