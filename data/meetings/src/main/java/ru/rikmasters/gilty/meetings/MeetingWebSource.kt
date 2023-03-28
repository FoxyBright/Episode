package ru.rikmasters.gilty.meetings

import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.models.Location
import ru.rikmasters.gilty.shared.models.Requirement
import ru.rikmasters.gilty.shared.models.User
import ru.rikmasters.gilty.shared.models.meets.*
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingWebSource: KtorSource() {
    
    private data class ShortLocation(
        val address: String,
        val place: String,
    )
    
    private data class Respond(
        val description: String?,
        val photoAccess: Boolean,
    )
    
    suspend fun setUserInterest(meets: List<String>) {
        patch("http://$HOST$PREFIX_URL/profile/categories") {
            url { meets.forEach { query("category_ids[]" to it) } }
        }
    }
    
    suspend fun notInteresting(meetId: String) {
        patch("http://$HOST$PREFIX_URL/meetings/$meetId/markAsNotInteresting")
    }
    
    suspend fun resetMeets() {
        post("http://$HOST$PREFIX_URL/meetings/reset")
    }
    
    suspend fun respondOfMeet(
        meetId: String,
        comment: String?,
        hidden: Boolean,
    ) {
        post("http://$HOST$PREFIX_URL/meetings/$meetId/responds") {
            setBody(Respond(comment, hidden))
        }
    }
    
    suspend fun leaveMeet(meetId: String) {
        patch("http://$HOST$PREFIX_URL/meetings/$meetId/leave")
    }
    
    suspend fun getMeetsList(
        count: Boolean,
        group: String,
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
    ) = get(
        "http://$HOST$PREFIX_URL/meetings${if(count) "/count" else ""}"
    ) {
        url {
            query("group" to group)
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
                "TODAY" -> time?.let { query("time" to it) }
                "AFTER" -> dates?.let { list ->
                    list.forEach {
                        query("dates[]" to it)
                    }
                }
            }
        }
    }!!
    
    suspend fun cancelMeet(meetId: String) {
        patch("http://$HOST$PREFIX_URL/meetings/$meetId/cancel")
    }
    
    suspend fun getUserActualMeets(
        userId: String,
    ) = get("http://$HOST$PREFIX_URL/users/$userId/meetings")!!
        .wrapped<List<Meeting>>().map { it.map() }
    
    suspend fun getDetailedMeet(
        meet: String,
    ) = get("http://$HOST$PREFIX_URL/meetings/$meet")!!
        .wrapped<DetailedMeetResponse>().map()
    
    suspend fun getMeetMembers(
        meet: String,
        excludeMe: Int,
    ) = try {
        get("http://$HOST$PREFIX_URL/meetings/$meet/members") {
            url { query("exclude_me" to "$excludeMe") }
        }!!.wrapped<List<User>>().map { it.map() }
    } catch(e: Exception) {
        emptyList()
    }
    
    suspend fun getOrientations() = try {
        get("http://$HOST$PREFIX_URL/orientations")!!
            .wrapped<List<OrientationModel>>()
    } catch(e: Exception) {
        emptyList()
    }
    
    suspend fun getLastPlaces() = try {
        get("http://$HOST$PREFIX_URL/meetings/places")!!
            .wrapped<List<ShortLocation>>()
            .map { Pair(it.address, it.place) }
    } catch(e: Exception) {
        emptyList()
    }
    
    suspend fun getPopularTags(
        categoriesId: List<String?>,
    ) = try {
        get("http://$HOST$PREFIX_URL/tags/popular") {
            url {
                categoriesId.forEach {
                    query("category_ids[]" to "$it")
                }
            }
        }!!.wrapped<List<TagModel>>()
    } catch(e: Exception) {
        emptyList()
    }
    
    suspend fun addNewTag(tag: String) {
        post("http://$HOST$PREFIX_URL/tags") {
            setBody(Tag(tag))
        }
    }
    
    suspend fun searchTags(tag: String) = try {
        get("http://$HOST$PREFIX_URL/tags/search") {
            url { query("query" to tag) }
        }!!.wrapped<List<TagModel>>()
    } catch(e: Exception) {
        emptyList()
    }
    
    suspend fun getCategoriesList() =
        get("http://$HOST$PREFIX_URL/categories")!!
            .wrapped<List<Category>>().map { it.map() }
    
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
    ) = post("http://$HOST$PREFIX_URL/meetings") {
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
    }!!.wrapped<DetailedMeetResponse>().id
}