package ru.rikmasters.gilty.auth.manager

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.model.DataStateTest
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
    
    suspend fun clearProfile() {
        profileStore.deleteProfile()
    }
    
    suspend fun userId() = withContext(IO) {
        profileStore.getProfile(false).id
    }
    
    suspend fun getProfile(forceWeb: Boolean = true) = withContext(IO) {
        profileStore.getProfile(forceWeb)
    }
    
    suspend fun getHidden(albumId: String) = withContext(IO) {
        val response = profileWebSource
            .getFiles(albumId).on(
                success = {
                    it.first.map { url ->
                        url.thumbnail?.url ?: ""
                    } to it.second
                },
                loading = { emptyList<String>() to 0 },
                error = { emptyList<String>() to 0 }
            )
        DataStateTest.Success(response)
    }
    
    suspend fun setAvatar(
        file: File, points: List<Float>,
    ) = withContext(IO) {
        profileWebSource.setUserAvatar(file, points)
    }
    
    suspend fun getUserCategories() = withContext(IO) {
        profileStore.getUserCategories(true)
    }
    
    suspend fun deleteHidden(files: List<String>) =
        withContext(IO) {
            files.forEach {
                profileStore.deleteHidden(
                    it.substring("thumbnails/", "?")
                )
            }
        }
    
    suspend fun addHidden(
        files: List<File>,
    ) = withContext(IO) {
        profileStore
            .addHidden(files)
    }
    
    suspend fun isNameOccupied(
        name: String,
    ) = withContext(IO) {
        profileWebSource
            .checkUserName(name)
    }
    
    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
    ) = withContext(IO) {
        profileStore.updateProfile(
            username, aboutMe, age, gender,
            OrientationModel("HETERO", "Гетеро")
        )
    }
}

private fun String.substring(
    after: String, before: String,
) = this.substringAfter(after)
    .substringBefore(before)