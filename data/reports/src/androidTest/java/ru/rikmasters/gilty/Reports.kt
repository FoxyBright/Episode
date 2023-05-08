package ru.rikmasters.gilty

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition

object Reports: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {}
    
    
    override fun Module.koin() {
        singleOf(::ReportsManager)
        singleOf(::ReportsWebSource)
    }
}