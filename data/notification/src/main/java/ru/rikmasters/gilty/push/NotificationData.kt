package ru.rikmasters.gilty.push

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition

object NotificationData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
    }
    
    override fun Module.koin() {
        singleOf(::NotificationWebSource)
        singleOf(::NotificationManager)
        singleOf(::NotificationPagingSource)
    }
}

