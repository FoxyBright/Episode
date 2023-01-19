package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.categories.CategoriesWebSource
import ru.rikmasters.gilty.auth.categories.Category
import ru.rikmasters.gilty.auth.profile.ProfileWebSource
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.GenderType
import java.io.File

class RegistrationManager(
    
    private val profileWebSource: ProfileWebSource,
    
    private val categoryWebSource: CategoriesWebSource

) {
    
    suspend fun getCategoriesList(): List<Category> =
        categoryWebSource.getCategoriesList()
    
    suspend fun isUserRegistered(): Boolean =
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