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
    
    private val store: ProfileStore,
) {
    
    suspend fun getUserCategories() =
        store.getUserCategories(false)
    
    suspend fun updateUserCategories() {
        store.updateUserCategories()
    }
    
    suspend fun getObservers(
        query: String,
        type: ObserversType,
    ) = web.getObservers(query, type)
    
    suspend fun getUser(id: String) =
        web.getUser(id)
    
    suspend fun clearProfile() {
        store.deleteProfile()
    }
    
    suspend fun getProfile(forceWeb: Boolean) =
        store.getProfile(forceWeb)
    
    val hiddenFlow = store.hiddenFlow()
    
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
    
    suspend fun getUserMeets(
        forceWeb: Boolean, type: MeetingsType,
    ) = store.getUserMeets(forceWeb, type)
    
    suspend fun unsubscribeFromUser(member: String) {
        web.unsubscribeFromUser(member)
    }
    
    suspend fun userUpdateData(
        username: String? = null,
        aboutMe: String? = null,
        age: Int? = null,
        gender: GenderType? = null,
        orientation: OrientationModel? = null,
    ) {
        store.updateProfile(
            username, aboutMe, age,
            gender, orientation
        )
    }
    
    suspend fun getResponds(type: RespondType) =
        web.getResponds(type)
    
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

