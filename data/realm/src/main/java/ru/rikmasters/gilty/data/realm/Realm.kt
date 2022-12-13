package ru.rikmasters.gilty.data.realm

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.module.DomainDefinition
import ru.rikmasters.gilty.data.realm.container.RealmContainerSource
import ru.rikmasters.gilty.data.realm.container.RealmContainerSourceImpl
import ru.rikmasters.gilty.data.realm.facade.RealmSourceFacade
import ru.rikmasters.gilty.data.realm.`object`.RealmSource
import ru.rikmasters.gilty.data.realm.`object`.RealmSourceImpl

object Realm: DomainDefinition() {
    
    override fun Module.koin() {
        singleOf(::RealmSourceFacade).bind<DbSource>()
        
        singleOf(::RealmSourceImpl).bind<RealmSource>()
        singleOf(::RealmContainerSourceImpl).bind<RealmContainerSource>()
    
        singleOf(::RealmManager)
    }
}