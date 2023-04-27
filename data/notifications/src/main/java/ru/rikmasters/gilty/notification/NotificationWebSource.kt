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
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped

class NotificationWebSource: KtorSource() {
    
    // выставление реакции на встречу или пользователю
    suspend fun putRatings(meetId: String, userId: String, emoji: String) {
        data class Ratings(val ratings: List<PutRatingRequest>)
        put("http://$HOST$PREFIX_URL/meetings/$meetId/ratings") {
            setBody(Ratings(listOf(PutRatingRequest(userId, emoji))))
        }
    }
    
    // удаление уведомления
    suspend fun deleteNotifications(
        notifyIds: List<String>,
        deleteAll: Int,
    ) {
        delete("http://$HOST$PREFIX_URL/notifications") {
            url {
                notifyIds.forEach { notify ->
                    query("notification_ids[]" to notify)
                }
                query("delete_all" to "$deleteAll")
            }
        }
    }
    
    // пометка уведомления как прочитанного
    suspend fun markNotifiesAsRead(
        notifyIds: List<String>,
        readAll: Boolean,
    ) {
        post("http://$HOST$PREFIX_URL/notifications/markAsRead") {
            MarkAsReadRequest(notifyIds, readAll)
        }
    }
    
    // получение списка возможных реакций на встречу или участника
    suspend fun getRatings() =
        get("http://$HOST$PREFIX_URL/ratings")
            ?.let { res ->
                if(res.status == OK)
                    res.wrapped<List<Rating>>().map { it.map() }
                else null
            } ?: emptyList()
    
    // получение списка уведомлений
    suspend
    
    fun getNotifications(
        page: Int, perPage: Int,
    ) = get("http://$HOST$PREFIX_URL/profile/notifications") {
        url {
            query("page" to "$page")
            query("per_page" to "$perPage")
        }
    }?.let { if(it.status == OK) it.paginateWrapped<List<Notification>>() else null }
        ?: throw IllegalArgumentException("Ошибка при попытке получения уведомлений")
}