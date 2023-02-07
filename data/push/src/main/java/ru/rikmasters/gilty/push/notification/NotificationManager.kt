package ru.rikmasters.gilty.push.notification

class NotificationManager(
    
    private val web: NotificationWebSource,
) {
    
    suspend fun deleteNotifications(
        notifyIds: List<String>,
        deleteAll: Boolean = false,
    ) {
        web.deleteNotifications(
            notifyIds,
            deleteAll.compareTo(false)
        )
    }
    
    suspend fun markNotifyAsRead(
        notifyIds: List<String>,
        readAll: Boolean = false,
    ) {
        web.markNotifiesAsRead(notifyIds, readAll)
    }
    
    suspend fun getNotification() =
        web.getNotifications()
}