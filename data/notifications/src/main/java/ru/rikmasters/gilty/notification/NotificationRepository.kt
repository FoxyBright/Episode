package ru.rikmasters.gilty.notification

import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.deleteAll
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.model.notification.NotificationModel
import ru.rikmasters.gilty.shared.models.Notification
import ru.rikmasters.gilty.shared.wrapper.ResponseWrapper.Paginator

class NotificationRepository(
    override val primarySource: DbSource,
    override val webSource: NotificationWebSource,
): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {
    
    // мапер листа DTO -> UI моделей
    private fun List<Notification>.map() = this.map { it.map() }
    
    // подгрузка уведомлений по страницам
    suspend fun getNotifications(
        page: Int, perPage: Int,
    ): Pair<List<NotificationModel>, Paginator> {
    
//        val first = page * perPage
//        val last = first + perPage
//        val list = primarySource.findAll<Notification>()
//
//        return if(last > list.size && list.isNotEmpty())
//            Pair(
//                list.slice(first..last).map(),
//                Paginator(perPage, page)
//            )
//        else
        
        // TODO репозиторий почему-то не отвечает на запросы пейджера
            return  upload(page, perPage)
    }
    
    private suspend fun upload(
        page: Int, perPage: Int,
    ): Pair<List<NotificationModel>, Paginator> {
        val list = webSource.getNotifications(page, perPage)
        primarySource.deleteAll<Notification>()
        primarySource.saveAll(list.first)
        return Pair(list.first.map(), list.second)
    }
}