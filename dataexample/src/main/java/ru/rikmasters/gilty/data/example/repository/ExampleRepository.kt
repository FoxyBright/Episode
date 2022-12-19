package ru.rikmasters.gilty.data.example.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.data.example.model.Door
import ru.rikmasters.gilty.data.example.model.ResponseWrapper
import ru.rikmasters.gilty.data.ktor.KtorSource

class ExampleRepository(
    
    override val webSource: KtorSource,
    
    override val primarySource: DbSource
    
): OfflineFirstRepository<WebSource, DbSource>(webSource, primarySource) {
    
    fun doorsFlow() = primarySource.listenAll(Door::class)
    
    suspend fun getDoors(forceWeb: Boolean = false) = background {
        if(!forceWeb) {
            log.v("Обычный запрос начинается с поиска в бд")
            val saved = primarySource.findAll<Door>()
            if(saved.isNotEmpty()) {
                log.v("Realm не пустой, возвращаем сохранённые данные")
                return@background saved
            }
            log.v("Realm пустой")
        } else log.v("PullToRefresh запрос игнорирует сохранённые данные")
        
        log.v("Сетевой запрос")
        val list = getDoorsWeb()
        
        log.v("При успешном сетевом запросе база очищается")
        primarySource.deleteAll<Door>()
        
        log.v("А затем сохраняются новые данные")
        primarySource.saveAll(list)
        
        list // то же самое, что и return@background list
    }
    
    // Можно вынести в отдельный класс-наследник KtorSource
    private suspend fun getDoorsWeb() =
        webSource.unauthorizedClient
            .get("http://cars.cprogroup.ru/api/rubetek/doors/")
            .body<ResponseWrapper<List<Door>>>()
            .data ?: emptyList()
}