package ru.rikmasters.gilty.meetings

import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.DataStateTest
import ru.rikmasters.gilty.shared.model.meeting.TagModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.models.*
import ru.rikmasters.gilty.shared.models.meets.*
import ru.rikmasters.gilty.shared.wrapper.coroutinesState
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
    ) = tryGet("http://$HOST$PREFIX_URL/location/cities") {
        url {
            if(query.isNotBlank()) query("query" to query)
            page?.let { query("page" to "$it") }
            perPage?.let { query("per_page" to "$it") }
            lat?.let { query("lat" to "$it") }
            lng?.let { query("lng" to "$it") }
            query("country" to getLocale())
            subjectId?.let { query("subject_id" to it) }
        }
    }.let {
        coroutinesState({ it }) {
            it.wrapped<List<City>>()
                .map { it.map() }
        }
    }
    
    suspend fun setUserInterest(meets: List<String>) =
        tryPatch("http://$HOST$PREFIX_URL/profile/categories") {
            url {
                meets.forEach {
                    query("category_ids[]" to it)
                }
            }
        }.let { coroutinesState({ it }) {} }
    
    suspend fun notInteresting(meetId: String) =
        tryPatch(
            "http://$HOST$PREFIX_URL/meetings/$meetId/markAsNotInteresting"
        ).let { coroutinesState({ it }) {} }
    
    suspend fun resetMeets() =
        tryPost("http://$HOST$PREFIX_URL/meetings/reset")
            .let { coroutinesState({ it }) {} }
    
    suspend fun respondOfMeet(
        meetId: String,
        comment: String?,
        hidden: Boolean,
    ) = tryPost(
        "http://$HOST$PREFIX_URL/meetings/$meetId/responds"
    ) { setBody(Respond(comment, hidden)) }
        .let { coroutinesState({ it }) {} }
    
    suspend fun leaveMeet(meetId: String) =
        tryPatch("http://$HOST$PREFIX_URL/meetings/$meetId/leave")
            .let { coroutinesState({ it }) {} }
    
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
    ) = tryGet(
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
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<MainMeetResponse>>().first.map { it.map() }
        }
    }
    
    suspend fun cancelMeet(meetId: String) =
        tryPatch("http://$HOST$PREFIX_URL/meetings/$meetId/cancel")
            .let { coroutinesState({ it }) {} }
    
    
    suspend fun getUserActualMeets(userId: String) =
        tryGet("http://$HOST$PREFIX_URL/users/$userId/meetings")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<List<Meeting>>()
                        .map { it.map() }
                }
            }
    
    suspend fun getDetailedMeet(meet: String) =
        tryGet("http://$HOST$PREFIX_URL/meetings/$meet")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<DetailedMeetResponse>().map()
                }
            }
    
    // TODO: По мере возможности заменить используемый сейчас метод выше этим методом
    suspend fun getDetailedMeetTest(meet: String) =
        tryGet("http://$HOST$PREFIX_URL/meetings/$meet")
    
    suspend fun getMeetMembers(
        meet: String,
        excludeMe: Int,
        page: Int,
        perPage: Int,
    ): DataStateTest<List<User>> = tryGet("http://$HOST$PREFIX_URL/meetings/$meet/members") {
        url {
            query("exclude_me" to "$excludeMe")
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }.let {
        coroutinesState({ it }) {
            it.wrapped<List<User>>()
        }
    }
    
    suspend fun getOrientations() =
        tryGet("http://$HOST$PREFIX_URL/orientations")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<List<OrientationModel>>()
                }
            }
    
    suspend fun getLastPlaces() =
        tryGet("http://$HOST$PREFIX_URL/meetings/places")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<List<Location>>()
                        .map { it.map() }
                }
            }
    
    suspend fun getPopularTags(categoriesId: List<String?>) =
        tryGet("http://$HOST$PREFIX_URL/tags/popular") {
            url {
                categoriesId.forEach {
                    query("category_ids[]" to "$it")
                }
            }
        }.let {
            coroutinesState({ it }) {
                it.wrapped<List<TagModel>>()
            }
        }
    
    suspend fun addNewTag(tag: String) =
        tryPost("http://$HOST$PREFIX_URL/tags") {
            setBody(Tag(tag))
        }.let { coroutinesState({ it }) {} }
    
    suspend fun searchTags(tag: String) =
        tryGet("http://$HOST$PREFIX_URL/tags/search") {
            url { query("query" to tag) }
        }.let {
            coroutinesState({ it }) {
                it.wrapped<List<TagModel>>()
            }
        }
    
    suspend fun getCategoriesList() =
        tryGet("http://$HOST$PREFIX_URL/categories")
            .let {
                coroutinesState({ it }) {
                    it.wrapped<List<Category>>()
                        .map { it.map() }
                }
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
    ) = tryPost(
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
        coroutinesState(
            request = { it },
            expectCode = 201
        ) { it.wrapped<DetailedMeetResponse>().id }
    }
}
