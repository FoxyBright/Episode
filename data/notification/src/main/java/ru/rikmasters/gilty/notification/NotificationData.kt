package ru.rikmasters.gilty.notification

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.shared.models.Notification

object NotificationData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<Notification>()
    }
    
    override fun Module.koin() {
        singleOf(::NotificationRepository)
        singleOf(::NotificationWebSource)
        singleOf(::NotificationManager)
    }
}

