package ru.rikmasters.gilty.profile

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.push.notification.NotificationManager

object ProfileData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
    }
    
    override fun Module.koin() {
        singleOf(::NotificationManager)
        singleOf(::ProfileWebSource)
        singleOf(::ProfileManager)
    }
}