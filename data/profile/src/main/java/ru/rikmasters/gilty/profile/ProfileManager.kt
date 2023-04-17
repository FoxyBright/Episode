package ru.rikmasters.gilty.profile

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class ProfileManager(
    private val web: ProfileWebSource,
    private val store: ProfileStore,
) {
    
    suspend fun storageProfile() = withContext(IO) {
        store.storageProfile()?.map()
    }
    
    suspend fun getUserCategories() = withContext(IO) {
        store.getUserCategories(false)
    }
    
    suspend fun updateUserCategories() {
        withContext(IO) {
            store.updateUserCategories()
        }
    }
    
    fun getObservers(
        query: String,
        type: ObserversType,
    ) = store.getObservers(query, type)
    
    suspend fun getUser(
        id: String,
    ) = withContext(IO) {
        web.getUser(id)
    }
    
    suspend fun clearProfile() {
        withContext(IO) {
            store.deleteProfile()
        }
    }
    
    @Suppress("unused")
    suspend fun getPhotos(
        albumId: String,
    ) = withContext(IO) {
        if(albumId.isNotBlank()) web.getFiles(albumId)
            .map { it.map() } else emptyList()
    }
    
    fun getPhotosPaging(albumId: String) =
        store.getFiles(albumId)
    
    suspend fun getProfile(
        forceWeb: Boolean = false,
    ) = withContext(IO) {
        store.getProfile(forceWeb)
    }
    
    fun getHiddenPhotos() = store.getUserHiddenPaging()
    
    suspend fun getProfileHiddens(
        forceWeb: Boolean,
    ) = withContext(IO) {
        store.getUserHidden(forceWeb)
    }
    
    suspend fun deleteHidden(imageId: String) {
        withContext(IO) {
            store.deleteHidden(imageId)
            getProfileHiddens(false)
        }
    }
    
    suspend fun deleteObserver(
        member: UserModel,
    ) {
        withContext(IO) {
            web.deleteObserver(member)
        }
    }
    
    suspend fun subscribeToUser(member: String) {
        withContext(IO) {
            web.subscribeToUser(member)
        }
    }
    
    fun getUserMeets(
        type: MeetingsType,
    ) = store.getUserMeets(type)
    
    suspend fun unsubscribeFromUser(
        member: String,
    ) {
        withContext(IO) {
            web.unsubscribeFromUser(member)
        }
    }
    
    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientation: OrientationModel? = null,
    ) {
        withContext(IO) {
            store.updateProfile(
                username, aboutMe,
                age, gender, orientation
            )
        }
    }
    
    fun getResponds(type: RespondType) =
        store.getResponds(type)
    
    fun getMeetResponds(meetId: String) =
        store.getMeetResponds(meetId)
    
    suspend fun deleteRespond(
        respondId: String,
    ) {
        withContext(IO) {
            web.deleteRespond(respondId)
        }
    }
    
    suspend fun acceptRespond(
        respondId: String,
    ) {
        withContext(IO) {
            web.acceptRespond(respondId)
        }
    }
    
    suspend fun cancelRespond(
        respondId: String,
    ) {
        withContext(IO) {
            web.cancelRespond(respondId)
        }
    }
    
    suspend fun deleteAccount() {
        withContext(IO) {
            web.deleteAccount()
            clearProfile()
        }
    }
}
