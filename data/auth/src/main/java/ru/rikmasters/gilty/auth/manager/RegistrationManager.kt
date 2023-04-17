package ru.rikmasters.gilty.auth.manager

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import java.io.File

class RegistrationManager(
    
    private val profileWebSource: ProfileWebSource,
    
    private val profileStore: ProfileStore,
) {
    
    suspend fun profileCompleted() = withContext(IO) {
        profileStore.checkCompletable()
    }
    
    suspend fun storageProfile() = withContext(IO) {
        profileStore.storageProfile()?.map()
    }
    
    suspend fun userId() = withContext(IO) {
        profileStore.getProfile(false).id
    }
    
    suspend fun getProfile() = withContext(IO) {
        profileStore.getProfile(true)
    }
    
    suspend fun getHidden(albumId: String) = withContext(IO) {
        profileWebSource.getFiles(albumId)
            .map { it.thumbnail?.url ?: "" }
    }
    
    suspend fun setAvatar(file: File, points: List<Float>) {
        withContext(IO) {
            profileWebSource.setUserAvatar(file, points)
        }
    }
    
    suspend fun getUserCategories() = withContext(IO) {
        profileStore.getUserCategories(true)
    }
    
    suspend fun deleteHidden(files: List<String>) {
        withContext(IO) {
            files.forEach { profileStore.deleteHidden(it) }
        }
    }
    
    suspend fun addHidden(files: List<File>) {
        withContext(IO) {
            profileStore.addHidden(files)
        }
    }
    
    suspend fun isNameOccupied(name: String) = withContext(IO) {
        profileWebSource.checkUserName(name)
    }
    
    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
    ) {
        withContext(IO) {
            profileStore.updateProfile(
                username, aboutMe, age, gender,
                OrientationModel("HETERO", "Гетеро")
            )
        }
    }
}