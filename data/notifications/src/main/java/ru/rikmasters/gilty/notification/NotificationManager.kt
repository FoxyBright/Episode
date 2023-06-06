package ru.rikmasters.gilty.notification

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.shared.model.image.EmojiModel

class NotificationManager(
    private val store: NotificationRepository,
    private val web: NotificationWebSource,
): CoroutineController() {
    
    fun getNotifications() = store.getNotifications()
    
    suspend fun deleteNotifications(
        notifyIds: List<String>,
        deleteAll: Boolean = false,
    ) = withContext(IO) {
        web.deleteNotifications(
            notifyIds,
            deleteAll.compareTo(false)
        )
    }
    
    suspend fun getRatings() =
        withContext(IO) { web.getRatings() }
    
    suspend fun putRatings(
        meetId: String,
        userId: String,
        emoji: EmojiModel,
    ) = withContext(IO) {
        web.putRatings(
            meetId = meetId,
            userId = userId,
            emoji = emoji.type
        )
    }
    
    suspend fun markNotifyAsRead(
        notifyIds: List<String> = emptyList(),
        readAll: Boolean = false,
    ) = withContext(IO) {
        web.markNotifiesAsRead(
            notifyIds = notifyIds,
            readAll = readAll
        )
    }
}