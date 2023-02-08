package ru.rikmasters.gilty.meetings

import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.models.MeetFiltersRequest
import ru.rikmasters.gilty.shared.models.request.meets.Location
import ru.rikmasters.gilty.shared.models.request.meets.Requirement
import ru.rikmasters.gilty.shared.models.response.meets.MainMeetResponse
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingManager(
    
    private val web: MeetingWebSource,
) {
    
    private data class MeetCount(val total: Int)
    
    private suspend inline fun <reified Type> getMeetings(
        filter: MeetFiltersRequest,
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
    
    suspend fun getMeetings(filter: MeetFiltersModel): List<MeetingModel> {
        return getMeetings<List<MainMeetResponse>>(
            MeetFiltersRequest(
                filter.group.value, filter.categories, filter.tags,
                filter.radius, filter.lat, filter.lng,
                filter.meetTypes, filter.onlyOnline, filter.genders,
                filter.conditions, filter.dates, filter.time
            ), false
        ).map { it.map() }
    }
    
    suspend fun getMeetCount(filter: MeetFiltersModel): Int =
        getMeetings<MeetCount>(
            MeetFiltersRequest(
                filter.group.value, filter.categories, filter.tags,
                filter.radius, filter.lat, filter.lng,
                filter.meetTypes, filter.onlyOnline, filter.genders,
                filter.conditions, filter.dates, filter.time
            ), true
        ).total
    
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
        hide: Boolean?,
        lat: Int?,
        lng: Int?,
        place: String?,
        address: String?,
        isPrivate: Boolean?,
        memberCount: Int?,
        requirementsType: String?,
        requirements: List<RequirementModel>?,
        withoutResponds: Boolean?,
    ) = web.addMeet(
        categoryId, type, isOnline,
        condition, price, photoAccess,
        chatForbidden, tags, description,
        dateTime, duration,
        if(isOnline != true)
            Location(hide, lat, lng, place, address)
        else null, isPrivate, memberCount, requirementsType,
        when {
            isPrivate == true -> null
            requirementsType == "ALL" -> {
                val req = requirements!!.first()
                listOf(
                    Requirement(
                        req.gender?.name, req.ageMin,
                        req.ageMax, (req.orientation!!.id)
                    )
                )
            }
            
            else -> requirements?.map {
                Requirement(
                    it.gender?.name, it.ageMin,
                    it.ageMax, (it.orientation!!.id)
                )
            }
        }, withoutResponds
    )
    
    suspend fun setUserInterest(meets: List<CategoryModel>) {
        web.setUserInterest(meets.map { it.id })
    }
}