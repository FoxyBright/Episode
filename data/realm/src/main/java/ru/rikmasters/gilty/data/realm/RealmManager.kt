package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.data.realm.model.RealmContainer
import kotlin.reflect.KClass

class RealmManager: Component {
    
    private val mainConfig by lazy {
        
        val realmObjects = env.specs
            .map { it.dbClass }
            .filterIsInstance<KClass<RealmObject>>()
            .toMutableSet()
    
        @Suppress("UNCHECKED_CAST")
        realmObjects.add(RealmContainer::class as KClass<RealmObject>)
    
        RealmConfiguration.Builder(realmObjects)
            .deleteRealmIfMigrationNeeded()
            .name("main.realm")
            .build()
    }
    
    private val mainRealm by lazy {
        Realm.open(mainConfig)
    }
    
    val realm get() = mainRealm
}