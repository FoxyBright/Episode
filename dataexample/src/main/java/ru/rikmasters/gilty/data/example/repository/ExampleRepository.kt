package ru.rikmasters.gilty.data.example.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.WebSource
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.data.example.model.*
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.realm.RealmSourceFacade
import java.util.UUID

class ExampleRepository(
    
    override val webSource: KtorSource,
    
    override val primarySource: RealmSourceFacade
    
): OfflineFirstRepository<WebSource, RealmSourceFacade>(webSource, primarySource) {
    
    suspend fun get(id: UUID): ExampleModel = background {
        
        val webEntity = ExampleWeb(id.toString(), "John", "21")
    
        val entity = webEntity.domain()
    
        primarySource.save(entity)
    
        primarySource.find(ExampleModel::class) ?: throw IllegalStateException("Не найдено")
    }
    
    suspend fun getDomainOnly(name: String): ExampleDomainOnlyModel = background {
        
        val entity = ExampleDomainOnlyModel(name, 21)
        
        primarySource.save(entity)
        
        primarySource.find(ExampleDomainOnlyModel::class) ?: throw IllegalStateException("Не найдено")
    }
    
    suspend fun getDoors(forceWeb: Boolean = false) = background {
        if(!forceWeb) {
            log.v("Обычный запрос начинается с поиска в бд")
            val saved = primarySource.findAll(Door::class)
            if(saved.isNotEmpty()) {
                log.v("Realm не пустой, возвращаем сохранённые данные")
                return@background saved
            }
            log.v("Realm пустой")
        } else log.v("PullToRefresh запрос игнорирует сохранённые данные")
        
        log.v("Сетевой запрос")
        val list = getDoorsWeb()
        
        log.v("Сохранение в случае")
        primarySource
        primarySource.saveAll(list)
        
        list
    }
    
    // Можно вынести в отдельный класс-наследник KtorSource
    private suspend fun getDoorsWeb() =
        webSource.unauthorizedClient
            .get("http://cars.cprogroup.ru/api/rubetek/doors/")
            .body<ResponseWrapper<List<Door>>>()
            .data ?: emptyList()
}