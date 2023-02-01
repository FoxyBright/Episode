package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.meetings.Location
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource
import ru.rikmasters.gilty.auth.meetings.Requirement
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.GenderType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

class MeetingManager(
    
    private val web: MeetingWebSource,
) {
    
    suspend fun getMeetCount(
        group: MeetingWebSource.MeetFilterGroup,
        categories: List<CategoryModel>? = null,
        tags: List<TagModel>? = null,
        radius: Int? = null,
        lat: Int? = null,
        lng: Int? = null,
        meetType: List<MeetType>? = null,
        onlyOnline: Boolean? = null,
        condition: List<ConditionType>? = null,
        gender: List<GenderType>? = null,
    ) = web.getMeetCount(
        group, categories?.map { it.id },
        tags?.map { it.id },
        radius, lat, lng,
        meetType?.map { it.name },
        onlyOnline?.compareTo(false),
        condition?.map { it.name },
        gender?.map { it.name }
    )
    
    suspend fun leaveMeet(meetId: String) {
        web.leaveMeet(meetId)
    }
    
    suspend fun cancelMeet(meetId: String) {
        web.cancelMeet(meetId)
    }
    
    @Suppress("unused")
    suspend fun addNewTag(tag: String) = web.addNewTag(tag)
    suspend fun getUserCategories() = web.getUserCategories()
    suspend fun getPopularTags(list: List<String?>) = web.getPopularTags(list)
    suspend fun getUserActualMeets(userId: String) = web.getUserActualMeets(userId)
    suspend fun getDetailedMeet(meetId: String) = web.getDetailedMeet(meetId)
    suspend fun searchTags(tag: String) = web.searchTags(tag)
    suspend fun getOrientations() = web.getOrientations()
    suspend fun getLastPlaces() = web.getLastPlaces()
    suspend fun getCategoriesList() = web.getCategoriesList()
    
    suspend fun getMeetMembers(meetId: String) = web.getMeetMembers(meetId)
    
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