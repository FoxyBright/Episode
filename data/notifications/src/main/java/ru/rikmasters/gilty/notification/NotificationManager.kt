package ru.rikmasters.gilty.notification

import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.notification.paginator.PagingManager
import ru.rikmasters.gilty.shared.model.image.EmojiModel
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationManager(
    private val store: NotificationRepository,
    private val web: NotificationWebSource,
): CoroutineController() {
    
    // получение новой страницы пагинации
    fun getNotifications() = store.getNotifications()
    
    // удаление уведомления
    suspend fun deleteNotifications(
        notifyIds: List<String>, deleteAll: Boolean = false,
    ) {
        web.deleteNotifications(
            notifyIds, deleteAll.compareTo(false)
        )
    }
    
    // получение списка возможных реакций на встречу или участника
    suspend fun getRatings() = web.getRatings()
    
    // выставление реакции на встречу или пользователю
    suspend fun putRatings(
        meetId: String, userId: String, emoji: EmojiModel,
    ) {
        web.putRatings(meetId, userId, emoji.type)
    }
    
    @Suppress("unused")
    // пометка уведомления как прочитанного
    suspend fun markNotifyAsRead(
        notifyIds: List<String>, readAll: Boolean = false,
    ) {
        web.markNotifiesAsRead(notifyIds, readAll)
    }
}