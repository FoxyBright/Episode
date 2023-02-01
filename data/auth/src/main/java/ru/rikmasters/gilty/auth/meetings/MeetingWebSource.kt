package ru.rikmasters.gilty.auth.meetings

import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource.MeetFilterGroup.AFTER
import ru.rikmasters.gilty.auth.meetings.MeetingWebSource.MeetFilterGroup.TODAY
import ru.rikmasters.gilty.auth.profile.MemberResponse
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.meeting.*
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingWebSource: KtorSource() {
    
    private data class ShortLocation(
        val address: String,
        val place: String,
    )
    
    suspend fun leaveMeet(meetId: String) {
        updateClientToken()
        client.patch(
            "http://$HOST$PREFIX_URL/meetings/$meetId/leave"
        ) {}
    }
    
    enum class MeetFilterGroup(val value: String) {
        AFTER("after"),
        TODAY("today");
        
        companion object {
            
            fun get(index: Int) = values()[index]
        }
    }
    
    suspend fun getMeetsList(
        count: Boolean,
        group: MeetFilterGroup,
        categories: List<String>? = null,
        tags: List<String>? = null,
        radius: Int? = null,
        lat: Int? = null,
        lng: Int? = null,
        meetTypes: List<String>? = null,
        onlyOnline: Int? = null,
        conditions: List<String>? = null,
        genders: List<String>? = null,
        dates: List<String>? = null,
        time: String? = null,
    ): HttpResponse {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/meetings${if(count) "/count" else ""}"
        ) {
            url {
                query("group" to group.value)
                categories?.let { list ->
                    list.forEach {
                        query("category_ids[]" to it)
                    }
                }
                tags?.let { list ->
                    list.forEach {
                        query("tag_ids[]" to it)
                    }
                }
                radius?.let { query("radius" to "$it") }
                lat?.let { query("lat" to "$it") }
                lng?.let { query("lng" to "$it") }
                meetTypes?.let { list ->
                    list.forEach {
                        query("meeting_type[]" to it)
                    }
                }
                onlyOnline?.let { query("online_only" to "$it") }
                conditions?.let { list ->
                    list.forEach {
                        query("condition[]" to it)
                    }
                }
                genders?.let { list ->
                    list.forEach {
                        query("gender[]" to it)
                    }
                }
                when(group) {
                    TODAY -> time?.let { query("time" to it) }
                    AFTER -> dates?.let { list ->
                        list.forEach {
                            query("dates[]" to it)
                        }
                    }
                }
            }
        }
    }
    
    suspend fun cancelMeet(meetId: String) {
        updateClientToken()
        client.patch(
            "http://$HOST$PREFIX_URL/meetings/$meetId/cancel"
        ) {}
    }
    
    suspend fun getUserActualMeets(
        userId: String,
    ): List<MeetingModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/users/$userId/meetings"
        ) {}.wrapped<List<MeetingResponse>>().map { it.map() }
    }
    
    suspend fun getDetailedMeet(
        meet: String,
    ): FullMeetingModel {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/meetings/$meet"
        ) {}.wrapped<DetailedMeetResponse>().map()
    }
    
    suspend fun getMeetMembers(
        meet: String,
    ): List<MemberModel> {
        updateClientToken()
        return try {
            client.get(
                "http://$HOST$PREFIX_URL/meetings/$meet/members"
            ) {}.wrapped<List<MemberResponse>>().map { it.map() }
        } catch(e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getOrientations(): List<OrientationModel> {
        updateClientToken()
        return try {
            client.get(
                "http://$HOST$PREFIX_URL/orientations"
            ) {}.wrapped()
        } catch(e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getLastPlaces(): List<Pair<String, String>> {
        updateClientToken()
        return try {
            client.get(
                "http://$HOST$PREFIX_URL/meetings/places"
            ) {}.wrapped<List<ShortLocation>>().map {
                Pair(it.address, it.place)
            }
        } catch(e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getPopularTags(
        categoriesId: List<String?>,
    ): List<TagModel> {
        updateClientToken()
        return try {
            client.get(
                "http://$HOST$PREFIX_URL/tags/popular"
            ) {
                url {
                    categoriesId.forEach {
                        query("category_ids[]" to "$it")
                    }
                }
            }.wrapped()
        } catch(e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addNewTag(tag: String) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/tags"
        ) { setBody(Tag(tag)) }
    }
    
    suspend fun searchTags(tag: String): List<TagModel> {
        updateClientToken()
        return try {
            client.get(
                "http://$HOST$PREFIX_URL/tags/search"
            ) {
                url { query("query" to tag) }
            }.wrapped()
        } catch(e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getCategoriesList(): List<CategoryModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/categories"
        ).wrapped<List<Category>>().map { it.map() }
    }
    
    suspend fun getUserCategories(): List<CategoryModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/categories"
        ).wrapped<List<Category>>().map { it.map() }
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
    ) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/meetings"
        ) {
            setBody(
                MeetingRequest(
                    categoryId, type, isOnline, condition,
                    price, photoAccess, chatForbidden,
                    tags, description, dateTime,
                    duration, location, isPrivate,
                    memberCount, requirementsType,
                    requirements, withoutResponds
                )
            )
        }
    }
}