package ru.rikmasters.gilty.notification

import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode.Companion.OK
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.HOST
import ru.rikmasters.gilty.data.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.notification.model.MarkAsReadRequest
import ru.rikmasters.gilty.notification.model.PutRatingRequest
import ru.rikmasters.gilty.shared.models.Notification
import ru.rikmasters.gilty.shared.models.Rating
import ru.rikmasters.gilty.shared.wrapper.coroutinesState
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped

class NotificationWebSource: KtorSource() {
    
    private data class Ratings(val ratings: List<PutRatingRequest>)
    
    suspend fun putRatings(
        meetId: String, userId: String, emoji: String,
    ) = tryPut("http://$HOST$PREFIX_URL/meetings/$meetId/ratings") {
        setBody(Ratings(listOf(PutRatingRequest(userId, emoji))))
    }.let { coroutinesState({ it }) { it.status == OK } }
    
    suspend fun deleteNotifications(
        notifyIds: List<String>, deleteAll: Int,
    ) = tryDelete("http://$HOST$PREFIX_URL/notifications") {
        url {
            notifyIds.forEach { notify ->
                query("notification_ids[]" to notify)
            }
            query("delete_all" to "$deleteAll")
        }
    }.let { coroutinesState({ it }) {} }
    
    suspend fun markNotifiesAsRead(
        notifyIds: List<String>, readAll: Boolean,
    ) = tryPost("http://$HOST$PREFIX_URL/notifications/markAsRead") {
        MarkAsReadRequest(notifyIds, readAll)
    }.let { coroutinesState({ it }) {} }
    
    suspend fun getRatings() = tryGet(
        "http://$HOST$PREFIX_URL/ratings"
    ).let {
        coroutinesState({ it }) {
            it.wrapped<List<Rating>>()
                .map { it.map() }
        }
    }
    
    suspend fun getNotifications(
        page: Int, perPage: Int,
    ) = tryGet("http://$HOST$PREFIX_URL/profile/notifications") {
        url {
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }.let {
        coroutinesState({ it }) {
            it.paginateWrapped<List<Notification>>().first
        }
    }
}