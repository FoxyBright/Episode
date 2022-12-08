package ru.rikmasters.gilty.data.realm

import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.module.DomainDefinition

object Realm: DomainDefinition() {
    
    override fun Module.koin() {
        singleOf(::RealmSource) {
            bind<DbSource>()
        }
        singleOf(::RealmManager)
    }
}