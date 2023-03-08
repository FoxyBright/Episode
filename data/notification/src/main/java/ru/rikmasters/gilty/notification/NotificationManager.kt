package ru.rikmasters.gilty.notification

import kotlinx.coroutines.CoroutineScope
import ru.rikmasters.gilty.shared.model.image.EmojiModel

class NotificationManager(
    private val store: NotificationRepository,
    private val web: NotificationWebSource,
) {
    
    // список уведомлений в потоке собраный пагинатором
    fun notifications(scope: CoroutineScope) = store.pagination(scope)
    
    // обновление списка уведомлений
    fun refresh() = store.refresh()
    
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