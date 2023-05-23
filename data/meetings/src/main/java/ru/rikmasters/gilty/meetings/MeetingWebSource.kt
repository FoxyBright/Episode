package ru.rikmasters.gilty.meetings

import android.content.res.Resources.getSystem
import android.util.Log
import androidx.core.os.ConfigurationCompat.getLocales
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.models.*
import ru.rikmasters.gilty.shared.models.meets.*
import ru.rikmasters.gilty.shared.wrapper.errorWrapped
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingWebSource: KtorSource() {
    
    private data class Respond(
        val description: String?,
        val photoAccess: Boolean,
    )
    
    private fun getLocale() = "RU"
    
    suspend fun getCities(
        query: String,
        page: Int?,
        perPage: Int?,
        lat: Double?,
        lng: Double?,
        subjectId: String?,
    ) = get("http://$HOST$PREFIX_URL/location/cities") {
        url {
            if(query.isNotBlank()) query("query" to query)
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
            lat?.let { query("lat" to "$it") }
            lng?.let { query("lng" to "$it") }
            query("country" to getLocale())
            subjectId?.let { query("subject_id" to it) }
        }
    }?.let { res ->
        if(res.status == OK)
            res.wrapped<List<City>>()
                .map { it.map() }
        else null
    } ?: emptyList()
    
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
        lat: Double? = null,
        lng: Double? = null,
        meetTypes: List<String>? = null,
        onlyOnline: Int? = null,
        conditions: List<String>? = null,
        genders: List<String>? = null,
        dates: List<String>? = null,
        time: String? = null,
        city: Int? = null,
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
                    query("tags[]" to it)
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
                "today" -> time?.let { query("time" to it) }
                "after" -> dates?.let { list ->
                    list.forEach {
                        query("dates[]" to it)
                    }
                }
            }
            city?.let { query("city_id" to "$it") }
            query("country" to getLocale())
        }
    }
    
    suspend fun cancelMeet(meetId: String) {
        patch("http://$HOST$PREFIX_URL/meetings/$meetId/cancel")
    }
    
    suspend fun getUserActualMeets(
        userId: String,
    ) = get("http://$HOST$PREFIX_URL/users/$userId/meetings")
        ?.let { res ->
            if(res.status == OK)
                res.wrapped<List<Meeting>>()
                    .map { it.map() }
            else null
        } ?: emptyList()
    
    suspend fun getDetailedMeet(
        meet: String,
    ) = get("http://$HOST$PREFIX_URL/meetings/$meet")
        ?.let {
            if(it.status == OK)
                it.wrapped<DetailedMeetResponse>().map()
            else null
        }

    // TODO: По мере возможности заменить используемый сейчас метод выше этим методом
    suspend fun getDetailedMeetTest(
        meet: String
    ) = tryGet("http://$HOST$PREFIX_URL/meetings/$meet").wrapped<DetailedMeetResponse>()
    
    suspend fun getMeetMembers(
        meet: String,
        excludeMe: Int,
        page: Int,
        perPage: Int,
    ) = get("http://$HOST$PREFIX_URL/meetings/$meet/members") {
        url {
            query("exclude_me" to "$excludeMe")
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }?.let {
        if(it.status == OK)
            it.wrapped<List<User>>()
        else null
    }
    
    suspend fun getOrientations() = get(
        "http://$HOST$PREFIX_URL/orientations"
    )?.let {
        if(it.status == OK)
            it.wrapped<List<OrientationModel>>()
        else null
    } ?: emptyList()
    
    suspend fun getLastPlaces() = get(
        "http://$HOST$PREFIX_URL/meetings/places"
    )?.let { res ->
        if(res.status == OK)
            res.wrapped<List<Location>>()
                .map { it.map() }
        else null
    } ?: emptyList()
    
    suspend fun getPopularTags(
        categoriesId: List<String?>,
    ) = get("http://$HOST$PREFIX_URL/tags/popular") {
        url {
            categoriesId.forEach {
                query("category_ids[]" to "$it")
            }
        }
    }?.let {
        if(it.status == OK)
            it.wrapped<List<TagModel>>()
        else null
    } ?: emptyList()
    
    suspend fun addNewTag(tag: String) {
        post("http://$HOST$PREFIX_URL/tags") {
            setBody(Tag(tag))
        }
    }
    
    suspend fun searchTags(tag: String) =
        get("http://$HOST$PREFIX_URL/tags/search") {
            url { query("query" to tag) }
        }?.let {
            if(it.status == OK)
                it.wrapped<List<TagModel>>()
            else null
        } ?: emptyList()
    
    suspend fun getCategoriesList() =
        get("http://$HOST$PREFIX_URL/categories")
            ?.let { res ->
                if(res.status == OK)
                    res.wrapped<List<Category>>()
                        .map { it.map() }
                else null
            } ?: emptyList()
    
    
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
    ) = unExpectPost(
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
    }.let {
        Log.d("TEST","ADD MEET RESPONSE ${it.status} WRAPPED ${it.wrapped<DetailedMeetResponse>()}")
        if(it.status == Created)
            it.wrapped<DetailedMeetResponse>().id
        else "error" + it.errorWrapped().error.message
    }
}
