package ru.rikmasters.gilty.meetings

import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.meetings.addmeet.AddMeetStorage
import ru.rikmasters.gilty.meetings.paging.MeetMembersPagingSource
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
        withContext(IO) {
            storage.clear()
        }
    }
    
    suspend fun getCities(
        query: String = "",
        page: Int? = null,
        perPage: Int? = null,
        lat: Double? = null,
        lng: Double? = null,
        subjectId: String? = null,
    ) = withContext(IO) {
        web.getCities(
            query, page, perPage,
            lat, lng, subjectId
        )
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
        withContext(IO) {
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
    }
    
    private suspend fun getMeetings(
        filter: MeetFiltersRequest,
        count: Boolean,
    ) = web.getMeetsList(
        count = count,
        group = filter.group,
        categories = filter.categories?.map { it.id },
        tags = filter.tags?.map { it.title },
        radius = filter.radius,
        lat = filter.lat,
        lng = filter.lng,
        meetTypes = filter.meetTypes?.map { it.name },
        onlyOnline = filter.onlyOnline?.compareTo(false),
        conditions = filter.conditions?.map { it.name },
        genders = filter.genders?.map { it.name },
        dates = filter.dates,
        time = filter.time,
        city = filter.city?.id
    )
    
    suspend fun getMeetings(
        filter: MeetFiltersModel,
    ) = withContext(IO) {
        getMeetings(
            MeetFiltersRequest(
                filter.group.value, filter.categories,
                filter.tags, filter.radius, filter.lat,
                filter.lng, filter.meetTypes,
                filter.onlyOnline, filter.genders,
                filter.conditions, filter.dates,
                filter.time, filter.city
            ), false
        )?.let { response ->
            if(response.status == OK)
                response.wrapped<List<MainMeetResponse>>()
                    .map { it.map() }
            else null
        } ?: emptyList()
    }
    
    suspend fun getMeetCount(
        filter: MeetFiltersModel,
    ) = withContext(IO) {
        getMeetings(
            MeetFiltersRequest(
                filter.group.value, filter.categories,
                filter.tags, filter.radius, filter.lat,
                filter.lng, filter.meetTypes,
                filter.onlyOnline, filter.genders,
                filter.conditions, filter.dates,
                filter.time, filter.city
            ), true
        )?.let {
            if(it.status == OK)
                it.wrapped<MeetCount>().total
            else null
        } ?: 0
    }
    
    suspend fun leaveMeet(meetId: String) {
        withContext(IO) {
            web.leaveMeet(meetId)
        }
    }
    
    suspend fun cancelMeet(meetId: String) {
        withContext(IO) {
            web.cancelMeet(meetId)
        }
    }
    
    @Suppress("unused")
    suspend fun addNewTag(tag: String) = withContext(IO) {
        web.addNewTag(tag)
    }
    
    suspend fun getPopularTags(
        list: List<String?>,
    ) = withContext(IO) {
        web.getPopularTags(list)
    }
    
    suspend fun getUserActualMeets(
        userId: String,
    ) = withContext(IO) {
        web.getUserActualMeets(userId)
    }
    
    suspend fun getDetailedMeet(
        meetId: String,
    ) = withContext(IO) {
        web.getDetailedMeet(meetId)
    }
    
    suspend fun searchTags(
        tag: String,
    ) = withContext(IO) {
        web.searchTags(tag)
    }
    
    suspend fun getOrientations() = withContext(IO) {
        web.getOrientations()
    }
    
    suspend fun getLastPlaces() = withContext(IO) {
        web.getLastPlaces()
    }
    
    suspend fun getCategoriesList() = withContext(IO) {
        web.getCategoriesList()
    }
    
    fun getMeetMembers(
        meetId: String,
        excludeMe: Boolean = false,
    ) = Pager(
        config = PagingConfig(
            pageSize = 15,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MeetMembersPagingSource(
                webSource = web,
                excludeMe = excludeMe.compareTo(false),
                meet = meetId
            )
        }
    ).flow
    
    suspend fun notInteresting(meetId: String) {
        withContext(IO) {
            web.notInteresting(meetId)
        }
    }
    
    suspend fun resetMeets() {
        withContext(IO) {
            web.resetMeets()
        }
    }
    
    suspend fun respondOfMeet(
        meetId: String,
        comment: String?,
        hidden: Boolean,
    ) {
        withContext(IO) {
            web.respondOfMeet(
                meetId, comment?.let {
                    it.ifBlank { null }
                }, hidden
            )
        }
    }
    
    suspend fun addMeet(meet: AddMeetModel) = withContext(IO) {
        web.addMeet(
            meet.category?.id, meet.type.name,
            meet.isOnline, meet.condition.name,
            try {
                meet.price.toInt()
            } catch(e: Exception) {
                null
            },
            meet.photoAccess,
            meet.chatForbidden, meet.tags.map { it.title },
            meet.description.ifEmpty { null },
            meet.dateTime,
            durationToMinutes(meet.duration),
            if(!meet.isOnline) Location(
                meet.hide,
                meet.lat,
                meet.lng,
                meet.place,
                meet.address
            ) else null,
            meet.isPrivate,
            try {
                meet.memberCount.toInt()
            } catch(e: Exception) {
                1
            },
            meet.requirementsType,
            when {
                meet.isPrivate -> null
                meet.requirementsType == "ALL" ->
                    listOf(meet.requirements.first().reqMap())
                
                else -> meet.requirements.map { it.reqMap() }
            },
            meet.withoutResponds
        )
    }
    
    suspend fun setUserInterest(
        meets: List<CategoryModel>,
    ) {
        withContext(IO) {
            web.setUserInterest(meets.map { it.id })
        }
    }
}

private fun RequirementModel.reqMap() = Requirement(
    gender?.name,
    ageMin,
    ageMax,
    orientation?.id ?: "HETERO"
)