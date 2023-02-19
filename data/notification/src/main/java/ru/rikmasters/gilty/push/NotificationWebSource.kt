package ru.rikmasters.gilty.push

import io.ktor.client.request.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.model.profile.RatingModel
import ru.rikmasters.gilty.shared.models.NotificationResponse
import ru.rikmasters.gilty.shared.models.Rating
import ru.rikmasters.gilty.shared.wrapper.wrapped

class NotificationWebSource: KtorSource() {
    
    private data class MarkAsRead(
        val notificationIds: List<String>,
        val readAll: Boolean,
    )
    
    private data class PutRating(
        val userId: String,
        val emoji_type: String,
    )
    
    suspend fun putRatings(meetId: String, userId: String, emoji: String) {
        
        data class Ratings(val ratings: List<PutRating>)
        
        updateClientToken()
        client.put(
            "http://$HOST$PREFIX_URL/meetings/$meetId/ratings"
        ) { setBody(Ratings(listOf(PutRating(userId, emoji)))) }
    }
    
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
    
    suspend fun markNotifiesAsRead(
        notifyIds: List<String>,
        readAll: Boolean,
    ) {
        updateClientToken()
        client.post(
            "http://$HOST$PREFIX_URL/notifications/markAsRead"
        ) { MarkAsRead(notifyIds, readAll) }
    }
    
    suspend fun getRatings(): List<RatingModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/ratings"
        ).wrapped<List<Rating>>().map { it.map() }
    }
    
    suspend fun getNotifications(page: Int): List<NotificationModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/notifications"
        ) { url { query("page" to "$page") } }
            .wrapped<List<NotificationResponse>>()
            .map { it.map() }
    }
}