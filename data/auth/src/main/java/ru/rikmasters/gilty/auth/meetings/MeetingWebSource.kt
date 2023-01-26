package ru.rikmasters.gilty.auth.meetings

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel
import ru.rikmasters.gilty.shared.wrapper.wrapped

class MeetingWebSource: KtorSource() {
    
    private data class Tag(val title: String)
    
    private data class ShortLocation(
        val address: String,
        val place: String,
    )
    
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
    ): List<String> {
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
            }.wrapped<List<Tag>>().map { it.title }
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
    
    suspend fun searchTags(tag: String): List<String> {
        updateClientToken()
        return try {
            client.get(
                "http://$HOST$PREFIX_URL/tags/search"
            ) {
                url { query("query" to tag) }
            }.wrapped<List<Tag>>().map { it.title }
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