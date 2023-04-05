package ru.rikmasters.gilty.meetings

import ru.rikmasters.gilty.meetings.addmeet.AddMeetStorage
import ru.rikmasters.gilty.shared.common.extentions.durationToMinutes
import ru.rikmasters.gilty.shared.model.enumeration.ConditionType
import ru.rikmasters.gilty.shared.model.enumeration.MeetType
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.models.*
import ru.rikmasters.gilty.shared.models.meets.*
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingManager(
    
    private val storage: AddMeetStorage,
    
    private val web: MeetingWebSource,
) {
    
    private data class MeetCount(val total: Int)
    
    val addMeetFlow = storage.addMeetFlow
    
    suspend fun clearAddMeet() {
        storage.clear()
    }
    
    suspend fun update(
        category: CategoryModel? = null,
        type: MeetType? = null,
        isOnline: Boolean? = null,
        condition: ConditionType? = null,
        price: String? = null,
        photoAccess: Boolean? = null,
        chatForbidden: Boolean? = null,
        tags: List<TagModel>? = null,
        description: String? = null,
        dateTime: String? = null,
        duration: String? = null,
        hide: Boolean? = null,
        lat: Double? = null,
        lng: Double? = null,
        place: String? = null,
        address: String? = null,
        isPrivate: Boolean? = null,
        memberCount: String? = null,
        requirementsType: String? = null,
        requirements: List<RequirementModel>? = null,
        withoutResponds: Boolean? = null,
        memberLimited: Boolean? = null,
    ) {
        storage.update(
            category, type, isOnline, condition,
            price, photoAccess, chatForbidden, tags,
            description, dateTime, duration, hide,
            lat, lng, place, address, isPrivate,
            memberCount, requirementsType,
            requirements, withoutResponds,
            memberLimited
        )
    }
    
    private suspend inline fun <reified Type> getMeetings(
        filter: MeetFiltersRequest, count: Boolean,
    ): Type? {
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
        )?.wrapped()
    }
    
    suspend fun getMeetings(filter: MeetFiltersModel): List<MeetingModel> {
        return getMeetings<List<MainMeetResponse>>(
            MeetFiltersRequest(
                filter.group.value, filter.categories, filter.tags,
                filter.radius, filter.lat, filter.lng,
                filter.meetTypes, filter.onlyOnline, filter.genders,
                filter.conditions, filter.dates, filter.time
            ), false
        )?.map { it.map() } ?: emptyList()
    }
    
    suspend fun getMeetCount(filter: MeetFiltersModel): Int =
        getMeetings<MeetCount>(
            MeetFiltersRequest(
                filter.group.value, filter.categories, filter.tags,
                filter.radius, filter.lat, filter.lng,
                filter.meetTypes, filter.onlyOnline, filter.genders,
                filter.conditions, filter.dates, filter.time
            ), true
        )?.total ?: 0
    
    suspend fun leaveMeet(meetId: String) {
        web.leaveMeet(meetId)
    }
    
    suspend fun cancelMeet(meetId: String) {
        web.cancelMeet(meetId)
    }
    
    @Suppress("unused")
    suspend fun addNewTag(tag: String) = web.addNewTag(tag)
    suspend fun getPopularTags(list: List<String?>) = web.getPopularTags(list)
    suspend fun getUserActualMeets(userId: String) = web.getUserActualMeets(userId)
    suspend fun getDetailedMeet(meetId: String) = web.getDetailedMeet(meetId)
    suspend fun searchTags(tag: String) = web.searchTags(tag)
    suspend fun getOrientations() = web.getOrientations()
    suspend fun getLastPlaces() = web.getLastPlaces()
    suspend fun getCategoriesList() = web.getCategoriesList()
    
    suspend fun getMeetMembers(
        meetId: String, excludeMe: Boolean = false,
    ) = web.getMeetMembers(meetId, excludeMe.compareTo(false))
    
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
    
    suspend fun addMeet(meet: AddMeetModel) = web.addMeet(
        meet.category?.id, meet.type.name,
        meet.isOnline, meet.condition.name, try {
            meet.price.toInt()
        } catch(e: Exception) {
            null
        }, meet.photoAccess,
        meet.chatForbidden, meet.tags.map { it.title },
        meet.description.ifEmpty { null },
        meet.dateTime,
        durationToMinutes(meet.duration),
        if(!meet.isOnline) Location(
            meet.hide, meet.lat, meet.lng,
            meet.place, meet.address
        ) else null, meet.isPrivate, try {
            meet.memberCount.toInt()
        } catch(e: Exception) {
            1
        }, meet.requirementsType, when {
            meet.isPrivate -> null
            meet.requirementsType == "ALL" ->
                listOf(meet.requirements.first().reqMap())
            
            else -> meet.requirements.map { it.reqMap() }
        }, meet.withoutResponds
    )
    
    suspend fun setUserInterest(meets: List<CategoryModel>) {
        web.setUserInterest(meets.map { it.id })
    }
}

private fun RequirementModel.reqMap() = Requirement(
    gender?.name, ageMin,
    ageMax, orientation?.id ?: "HETERO"
)