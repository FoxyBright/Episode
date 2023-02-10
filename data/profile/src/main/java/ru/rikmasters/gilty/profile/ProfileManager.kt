package ru.rikmasters.gilty.profile

import ru.rikmasters.gilty.profile.ProfileWebSource.HiddenType
import ru.rikmasters.gilty.profile.ProfileWebSource.HiddenType.MY
import ru.rikmasters.gilty.profile.ProfileWebSource.MeetingsType
import ru.rikmasters.gilty.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.RespondType
import ru.rikmasters.gilty.shared.model.meeting.UserModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class ProfileManager(
    
    private val web: ProfileWebSource,
) {
    
    suspend fun getObservers(type: ObserversType) =
        web.getObservers(type)
    
    suspend fun getUser(id: String) =
        web.getUser(id)
    
    suspend fun getProfile() =
        web.getUserData()
    
    suspend fun getProfileHiddens(
        type: HiddenType = MY,
        albumId: String? = null,
    ) = web.getProfileHiddens(type, albumId).map { it.map() }
    
    suspend fun deleteHidden(image: AvatarModel) {
        web.deleteHidden(image)
    }
    
    suspend fun deleteObserver(member: UserModel) {
        web.deleteObserver(member)
    }
    
    suspend fun subscribeToUser(member: String) {
        web.subscribeToUser(member)
    }
    
    suspend fun getUserMeets(type: MeetingsType) =
        web.getUserMeets(type)
    
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
        web.setUserData(
            username, aboutMe, age,
            gender, orientation?.id
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
    }
}