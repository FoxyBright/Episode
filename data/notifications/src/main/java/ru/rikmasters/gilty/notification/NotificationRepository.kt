package ru.rikmasters.gilty.notification

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.notification.paging.NotificationListPagingSource
import ru.rikmasters.gilty.shared.model.notification.NotificationModel

class NotificationRepository(
    override val primarySource: DbSource,
    override val webSource: NotificationWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {
    
    // подгрузка уведомлений по страницам
    fun getNotifications(): Flow<PagingData<NotificationModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                NotificationListPagingSource(
                    webSource = webSource
                )
            }
        ).flow
    }
}