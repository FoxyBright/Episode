package ru.rikmasters.gilty.profile

import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.profile.models.MeetingsType
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class ProfileManager(

    private val web: ProfileWebSource,

    private val store: ProfileStore
) {

    suspend fun checkProfileStore() = store.checkProfileStore()

    suspend fun getUserCategories() =
        store.getUserCategories(false)

    suspend fun updateUserCategories() {
        store.updateUserCategories()
    }

    fun getObservers(
        query: String,
        type: ObserversType
    ) = store.getObservers(query, type)

    suspend fun getUser(id: String) =
        web.getUser(id)

    suspend fun clearProfile() {
        store.deleteProfile()
    }
    
    suspend fun getPhotos(albumId: String) =
        if(albumId.isNotBlank()) web.getFiles(albumId)
            .map { it.map() } else emptyList()

    fun getPhotosPaging(albumId: String) =
        store.getFiles(albumId)
    
    suspend fun getProfile(forceWeb: Boolean = false) =
        store.getProfile(forceWeb)

    fun getHiddenPhotos() = store.getUserHiddenPaging()

    suspend fun getProfileHiddens(forceWeb: Boolean) =
        store.getUserHidden(forceWeb)

    suspend fun deleteHidden(imageId: String) {
        store.deleteHidden(imageId)
        getProfileHiddens(false)
    }

    suspend fun deleteObserver(member: UserModel) {
        web.deleteObserver(member)
    }

    suspend fun subscribeToUser(member: String) {
        web.subscribeToUser(member)
    }

    fun getUserMeets(
        type: MeetingsType
    ) = store.getUserMeets(type)

    suspend fun unsubscribeFromUser(member: String) {
        web.unsubscribeFromUser(member)
    }

    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientation: OrientationModel? = null
    ) {
        store.updateProfile(
            username,
            aboutMe,
            age,
            gender,
            orientation
        )
    }

    fun getResponds(type: RespondType) =
        store.getResponds(type)

    fun getMeetResponds(meetId: String) =
        store.getMeetResponds(meetId)

    suspend fun deleteRespond(respondId: String) {
        web.deleteRespond(respondId)
    }

    suspend fun acceptRespond(respondId: String) {
        web.acceptRespond(respondId)
    }

    suspend fun cancelRespond(respondId: String) {
        web.cancelRespond(respondId)
    }

    suspend fun deleteAccount() {
        web.deleteAccount()
        clearProfile()
    }
}
