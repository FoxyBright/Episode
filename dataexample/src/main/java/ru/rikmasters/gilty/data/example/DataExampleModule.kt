package ru.rikmasters.gilty.data.example

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.data.example.model.*
import ru.rikmasters.gilty.data.example.repository.ExampleRepository

object DataExampleModule: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity(ExampleModel::class) {
            db(ExampleDb::class)
            web(ExampleWeb::class)
        }
        
        entity<ExampleDomainOnlyModel>()
    }
    
    override fun Module.koin() {
        singleOf(::ExampleRepository)
    }
}