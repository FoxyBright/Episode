package ru.rikmasters.gilty.profile

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.notification.NotificationManager
import ru.rikmasters.gilty.profile.models.ProfileCategories
import ru.rikmasters.gilty.profile.models.ProfileMeets
import ru.rikmasters.gilty.profile.repository.ProfileStore
import ru.rikmasters.gilty.shared.models.Avatar
import ru.rikmasters.gilty.shared.models.AvatarAmount

object ProfileData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<ProfileCategories>()
        entity<ProfileMeets>()
        entity<Avatar>()
        entity<AvatarAmount>()
    }
    
    override fun Module.koin() {
        singleOf(::NotificationManager)
        singleOf(::ProfileWebSource)
        singleOf(::ProfileManager)
        singleOf(::ProfileStore)
    }
}