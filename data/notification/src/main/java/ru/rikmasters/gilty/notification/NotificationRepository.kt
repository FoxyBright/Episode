package ru.rikmasters.gilty.notification

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.deleteAll
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.notification.paginator.Paginator
import ru.rikmasters.gilty.notification.paginator.PagingManager
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.models.Notification
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper

class NotificationRepository(
    override val primarySource: DbSource,
    override val webSource: NotificationWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {
    
    // мапер листа DTO -> UI моделей
    private fun List<Notification>.map() = this.map { it.map() }
    
    // менеджер пагинации для уведомлений
    private class NotificationPagingManager(
        private val source: NotificationRepository,
    ): PagingManager<NotificationModel> {
        
        // метод получения списка уведомлений
        override suspend fun getPage(
            page: Int, perPage: Int,
        ) = Pair(
            source.uploadNotifications(page, perPage),
            ResponseWrapper.Paginator(page, perPage)
        )
    }
    
    // источник пагинации для уведомлений
    private class NotificationPagingSource(
        manager: NotificationPagingManager,
    ): Paginator<NotificationModel, NotificationPagingManager>(manager)
    
    // последний используемый источник пагинации
    private var source: NotificationPagingSource? = null
    
    // создание нового источника пагинации
    private fun newSource(): NotificationPagingSource {
        source?.invalidate()
        source = NotificationPagingSource(
            NotificationPagingManager((this))
        ); return source!!
    }
    
    // обновление списка уведомлений
    fun refresh() {
        source?.invalidate()
        source = null
    }
    
    // пагинация для уведомлений
    fun pagination(scope: CoroutineScope) =
        Pager(PagingConfig(5))
        { newSource() }.flow.cachedIn(scope)
    
    // подгрузка уведомлений по страницам
    private suspend fun uploadNotifications(
        page: Int, perPage: Int,
    ): List<NotificationModel> {
        val list = webSource.getNotifications(page, perPage).first
        primarySource.deleteAll<Notification>()
        primarySource.saveAll(list)
        return list.map()
    }
}