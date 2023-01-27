package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.profile.ProfileWebSource
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.HiddenType
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.HiddenType.MY
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.MeetingsType
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

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
    
    suspend fun deleteObserver(member: MemberModel) {
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
}