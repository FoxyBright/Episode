package ru.rikmasters.gilty.auth.manager

import androidx.compose.ui.graphics.ImageBitmap
import ru.rikmasters.gilty.meetings.MeetingWebSource
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
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
    
    suspend fun getProfile() =
        profileStore.getProfile(true)
    
    suspend fun setAvatar(file: File, points: List<Float>) {
        profileWebSource.setUserAvatar(file, points)
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
            username, aboutMe, age, gender,
            OrientationModel("HETERO", "Гетеро")
        )
    }
}