package ru.rikmasters.gilty.push.notification

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig.HOST
import ru.rikmasters.gilty.shared.BuildConfig.PREFIX_URL
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.models.response.notification.NotificationResponse
import ru.rikmasters.gilty.shared.wrapper.wrapped

class NotificationWebSource: KtorSource() {
    
    private data class MarkAsRead(
        val notificationIds: List<String>,
        val readAll: Boolean,
    )
    
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
    
    suspend fun getNotifications(): List<NotificationModel> {
        updateClientToken()
        return client.get(
            "http://$HOST$PREFIX_URL/profile/notifications"
        ).wrapped<List<NotificationResponse>>().map { it.map() }
    }
}