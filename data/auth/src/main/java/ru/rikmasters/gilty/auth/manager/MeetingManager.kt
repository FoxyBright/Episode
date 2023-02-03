package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.meetings.*
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingManager(
    
    private val web: MeetingWebSource,
) {
    
    data class MeetCount(val total: Int)
    
    private suspend inline fun <reified Type> getMeetings(
        filter: MeetFilters,
        count: Boolean,
    ): Type {
        return web.getMeetsList(
            count = count,
            group = filter.group,
            categories = filter.categories?.map { it.id },
            tags = filter.tags?.map { it.id },
            radius = filter.radius,
            lat = filter.lat,
            lng = filter.lng,
            meetTypes = filter.meetTypes?.map { it.name },
            onlyOnline = filter.onlyOnline?.compareTo(false),
            conditions = filter.conditions?.map { it.name },
            genders = filter.genders?.map { it.name },
            dates = filter.dates,
            time = filter.time
        ).wrapped()
    }
    
    suspend fun getMeetings(filter: MeetFilters): List<MeetingModel> {
        return getMeetings<List<MainMeetResponse>>(
            filter, false
        ).map { it.map() }
    }
    
    suspend fun getMeetCount(filter: MeetFilters): Int =
        getMeetings<MeetCount>(filter, true).total
    
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
    
    suspend fun notInteresting(meetId: String) {
        web.notInteresting(meetId)
    }
    
    suspend fun resetMeets() {
        web.resetMeets()
    }
    
    suspend fun respondOfMeet(
        meetId: String, comment: String?, hidden: Boolean,
    ) {
        web.respondOfMeet(meetId, comment, hidden)
    }
    
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
    ) = web.addMeet(
        categoryId, type, isOnline,
        condition, price, photoAccess,
        chatForbidden, tags, description,
        dateTime, duration, location,
        isPrivate, memberCount, requirementsType,
        requirements, withoutResponds
    )
}