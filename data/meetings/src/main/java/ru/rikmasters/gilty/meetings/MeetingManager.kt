package ru.rikmasters.gilty.meetings

import androidx.paging.Pager
import androidx.paging.PagingConfig
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
import ru.rikmasters.gilty.shared.wrapper.coroutinesState
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingManager(
    private val storage: AddMeetStorage,
    private val web: MeetingWebSource,
    private val store: MeetingRepository,
) {
    
    val addMeetFlow = storage.addMeetFlow
    suspend fun getMeetingsPaging(
        filter: MeetFiltersModel,
        count: Boolean,
        page: Int,
    ) = store.getMeetings(
        filter = MeetFiltersRequest(
            group = filter.group.value,
            categories = filter.categories,
            tags = filter.tags,
            radius = filter.radius,
            lat = filter.lat,
            lng = filter.lng,
            meetTypes = filter.meetTypes,
            onlyOnline = filter.onlyOnline,
            genders = filter.genders,
            conditions = filter.conditions,
            dates = filter.dates,
            time = filter.time,
            city = filter.city
        ),
        page = page,
        count = count
    )
    
    suspend fun clearAddMeet() =
        withContext(IO) { storage.clear() }
    
    suspend fun getCities(
        query: String = "",
        page: Int? = null,
        perPage: Int? = null,
        lat: Double? = null,
        lng: Double? = null,
        subjectId: String? = null,
    ) = withContext(IO) {
        web.getCities(
            query = query,
            page = page,
            perPage = perPage,
            lat = lat,
            lng = lng,
            subjectId = subjectId
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
    ) = withContext(IO) {
        storage.update(
            category = category,
            type = type,
            isOnline = isOnline,
            condition = condition,
            price = price,
            photoAccess = photoAccess,
            chatForbidden = chatForbidden,
            tags = tags,
            description = description,
            dateTime = dateTime,
            duration = duration,
            hide = hide,
            lat = lat,
            lng = lng,
            place = place,
            address = address,
            isPrivate = isPrivate,
            memberCount = memberCount,
            requirementsType = requirementsType,
            requirements = requirements,
            withoutResponds = withoutResponds,
            memberLimited = memberLimited
        )
    }
    
    suspend fun leaveMeet(meetId: String) =
        withContext(IO) { web.leaveMeet(meetId) }
    
    suspend fun cancelMeet(meetId: String) =
        withContext(IO) { web.cancelMeet(meetId) }
    
    @Suppress("unused")
    suspend fun addNewTag(tag: String) =
        withContext(IO) { web.addNewTag(tag) }
    
    suspend fun getPopularTags(list: List<String?>) =
        withContext(IO) { web.getPopularTags(list) }
    
    suspend fun getUserActualMeets(userId: String) =
        withContext(IO) { web.getUserActualMeets(userId) }
    
    suspend fun getDetailedMeet(meetId: String) =
        withContext(IO) { web.getDetailedMeet(meetId) }
    
    // TODO: По мере возможности заменить
    //  используемый сейчас метод выше этим методом
    suspend fun getDetailedMeetTest(
        meetId: String,
    ) = web.getDetailedMeetTest(meetId).let {
        coroutinesState({ it }) {
            it.wrapped<FullMeetingModel>()
        }
    }
    
    suspend fun getCategoriesList() =
        withContext(IO) { web.getCategoriesList() }
    
    suspend fun getOrientations() =
        withContext(IO) { web.getOrientations() }
    
    suspend fun getLastPlaces() =
        withContext(IO) { web.getLastPlaces() }
    
    suspend fun searchTags(tag: String) =
        withContext(IO) { web.searchTags(tag) }
    
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
    
    suspend fun notInteresting(meetId: String) =
        withContext(IO) { web.notInteresting(meetId) }
    
    suspend fun resetMeets() =
        withContext(IO) { web.resetMeets() }
    
    suspend fun respondOfMeet(
        meetId: String,
        comment: String?,
        hidden: Boolean,
    ) = withContext(IO) {
        web.respondOfMeet(
            meetId = meetId,
            comment = comment?.let {
                it.ifBlank { null }
            },
            hidden = hidden
        )
    }
    
    suspend fun addMeet(
        meet: AddMeetModel,
    ) = withContext(IO) {
        web.addMeet(
            categoryId = meet.category?.id,
            type = meet.type.name,
            isOnline = meet.isOnline,
            condition = meet.condition.name,
            price = try {
                meet.price.toInt()
            } catch(e: Exception) {
                null
            },
            photoAccess = meet.photoAccess,
            chatForbidden = meet.chatForbidden,
            tags = meet.tags.map { it.title },
            description = meet.description.ifEmpty { null },
            dateTime = meet.dateTime,
            duration = durationToMinutes(meet.duration),
            location = if(!meet.isOnline) Location(
                meet.hide,
                meet.lat,
                meet.lng,
                meet.place,
                meet.address
            ) else null,
            isPrivate = meet.isPrivate,
            memberCount = try {
                meet.memberCount.toInt()
            } catch(e: Exception) {
                1
            },
            requirementsType = meet.requirementsType,
            requirements = when {
                meet.isPrivate -> null
                meet.requirementsType == "ALL" ->
                    listOf(meet.requirements.first().reqMap())
                
                else -> meet.requirements.map { it.reqMap() }
            },
            withoutResponds = meet.withoutResponds
        )
    }
    
    suspend fun kickMember(
        meetId: String,
        userId: String,
    ) = withContext(IO) {
        web.kickMember(meetId, userId)
    }
    
    suspend fun setUserInterest(meets: List<CategoryModel>) =
        withContext(IO) { web.setUserInterest(meets.map { it.id }) }
}

private fun RequirementModel.reqMap() = Requirement(
    gender = gender?.name,
    ageMin = ageMin,
    ageMax = ageMax,
    orientationId = orientation?.id ?: "HETERO"
)