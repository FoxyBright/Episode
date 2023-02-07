package ru.rikmasters.gilty.push

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.push.notification.NotificationManager
import ru.rikmasters.gilty.push.notification.NotificationWebSource

object Push: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
    }
    
    override fun Module.koin() {
        singleOf(::NotificationWebSource)
        singleOf(::NotificationManager)
    }
}

