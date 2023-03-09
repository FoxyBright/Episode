package ru.rikmasters.gilty.notification

import io.ktor.client.request.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.notification.model.MarkAsReadRequest
import ru.rikmasters.gilty.notification.model.PutRatingRequest
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.models.Notification
import ru.rikmasters.gilty.shared.models.Rating
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper.Paginator
import ru.rikmasters.gilty.shared.wrapper.paginateWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped

class NotificationWebSource: KtorSource() {
    
    // выставление реакции на встречу или пользователю
    suspend fun putRatings(meetId: String, userId: String, emoji: String) {
        data class Ratings(val ratings: List<PutRatingRequest>)
        updateClientToken()
        client.put(
            "http://$HOST$PREFIX_URL/meetings/$meetId/ratings"
        ) { setBody(Ratings(listOf(PutRatingRequest(userId, emoji)))) }
    }
    
    // удаление уведомления
    suspend fun deleteNotifications(
        notifyIds: List<String>,
        deleteAll: Int,
    ) {
        updateClientToken()
        client.delete(
            "http://$HOST$PREFIX_URL/notifications"
        ) {
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
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/notifications/markAsRead"
        ) { MarkAsReadRequest(notifyIds, readAll) }
    }
    
    // получение списка возможных реакций на встречу или участника
    suspend fun getRatings(): List<RatingModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/ratings"
        ).wrapped<List<Rating>>().map { it.map() }
    }
    
    // получение списка уведомлений
    suspend fun getNotifications(
        page: Int, perPage: Int,
    ): Pair<List<Notification>, Paginator> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/notifications"
        ) {
            url {
                query("page" to "$page")
                query("per_page" to "$perPage")
            }
        }.paginateWrapped()
    }
}