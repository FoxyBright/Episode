package ru.rikmasters.gilty.data.example

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.data.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.data.example.model.ExampleDb
import ru.rikmasters.gilty.data.example.model.ExampleModel
import ru.rikmasters.gilty.data.example.model.ExampleWeb

object DataExampleModule: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity(ExampleModel::class) {
            db(ExampleDb::class)
            web(ExampleWeb::class)
        }
    }
    
    override fun Module.koin() {
    
    }
}