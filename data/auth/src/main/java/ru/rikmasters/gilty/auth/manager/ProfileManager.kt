package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.profile.ProfileWebSource
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.MeetingsType
import ru.rikmasters.gilty.auth.profile.ProfileWebSource.ObserversType
import ru.rikmasters.gilty.shared.model.meeting.MemberModel
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class ProfileManager(
    
    private val profileWebSource: ProfileWebSource

) {
    
    suspend fun getObservers(type: ObserversType) =
        profileWebSource.getObservers(type)
    
    suspend fun getProfile() =
        profileWebSource.getUserData()
    
    suspend fun getProfileHiddens() =
        profileWebSource.getProfileHiddens().map { it.map() }
    
    suspend fun deleteHidden(image: AvatarModel) {
        profileWebSource.deleteHidden(image)
    }
    
    suspend fun deleteObserver(member: MemberModel) {
        profileWebSource.deleteObserver(member)
    }
    
    suspend fun subscribeToUser(member: MemberModel) {
        profileWebSource.subscribeToUser(member)
    }
    
    suspend fun getUserMeets(type: MeetingsType) =
        profileWebSource.getUserMeets(type)
    
    suspend fun unsubscribeFromUser(member: MemberModel) {
        profileWebSource.unsubscribeFromUser(member)
    }
}