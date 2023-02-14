package ru.rikmasters.gilty.push

import ru.rikmasters.gilty.shared.image.EmojiModel

class NotificationManager(
    
    private val web: NotificationWebSource,
) {
    
    suspend fun deleteNotifications(
        notifyIds: List<String>, deleteAll: Boolean = false,
    ) {
        web.deleteNotifications(
            notifyIds, deleteAll.compareTo(false)
        )
    }
    
    suspend fun getRatings() = web.getRatings()
    
    suspend fun putRatings(
        meetId: String, userId: String, emoji: EmojiModel,
    ) {
        web.putRatings(meetId, userId, emoji.type)
    }
    
    suspend fun markNotifyAsRead(
        notifyIds: List<String>, readAll: Boolean = false,
    ) {
        web.markNotifiesAsRead(notifyIds, readAll)
    }
    
    suspend fun getNotification(page: Int) =
        web.getNotifications(page)
}