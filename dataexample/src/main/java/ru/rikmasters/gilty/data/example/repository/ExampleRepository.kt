package ru.rikmasters.gilty.data.example.repository

import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.WebSource
import ru.rikmasters.gilty.data.example.model.ExampleModel
import ru.rikmasters.gilty.data.example.model.ExampleWeb
import ru.rikmasters.gilty.data.realm.RealmSource
import java.util.UUID

class ExampleRepository(
    
    override val webSource: WebSource,
    
    override val primarySource: RealmSource
    
): OfflineFirstRepository<WebSource, RealmSource>(webSource, primarySource) {

    suspend fun get(id: UUID): ExampleModel = background {
    
        //primarySource.findById(id, ExampleModel::class)
        
        // TODO Getting from web
        val webEntity = ExampleWeb(id.toString(), "John", "21")
    
        val entity = webEntity.domain()
    
        save(entity)
    
        primarySource.findById(id, ExampleModel::class) ?: throw IllegalStateException()
    }
    
    private suspend fun save(entity: ExampleModel) {
        primarySource.save(entity)
    }
}