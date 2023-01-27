package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.meetings.Location
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource
import ru.rikmasters.gilty.auth.meetings.Requirement
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class MeetingManager(
    
    private val web: MeetingWebSource,
) {
    
    suspend fun getPopularTags(list: List<String?>) =
        web.getPopularTags(list)
    
    
    suspend fun getDetailedMeet(meetId: String) =
        web.getDetailedMeet(meetId)
    
    suspend fun getLastPlaces() =
        web.getLastPlaces()
    
    suspend fun getOrientations() =
        web.getOrientations()
    
    suspend fun searchTags(tag: String) =
        web.searchTags(tag)
    
    @Suppress("unused")
    suspend fun addNewTag(tag: String) =
        web.addNewTag(tag)
    
    suspend fun getCategoriesList(): List<CategoryModel> =
        web.getCategoriesList()
    
    suspend fun getMeetMembers(meetId: String) =
        web.getMeetMembers(meetId)
    
    suspend fun addMeet(
        categoryId: String?,
        type: String?,
        isOnline: Boolean?,
        condition: String?,
        price: Int?,
        photoAccess: Boolean?,
        chatForbidden: Boolean?,
        tags: List<String>?,
        description: String?,
        dateTime: String?,
        duration: Int?,
        location: Location?,
        isPrivate: Boolean?,
        memberCount: Int?,
        requirementsType: String?,
        requirements: List<Requirement>?,
        withoutResponds: Boolean?,
    ) {
        web.addMeet(
            categoryId, type, isOnline,
            condition, price, photoAccess,
            chatForbidden, tags, description,
            dateTime, duration, location,
            isPrivate, memberCount, requirementsType,
            requirements, withoutResponds
        )
    }
}