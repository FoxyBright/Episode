package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import ru.rikmasters.gilty.core.common.Component
import kotlin.reflect.KClass

class RealmManager: Component {
    
    private val config by lazy {
        
        val realmObjects = env.specs
            .map { it.dbClass }
            .filterIsInstance<KClass<RealmObject>>()
            .toMutableSet()
    
        @Suppress("UNCHECKED_CAST")
        realmObjects.add(RealmContainer::class as KClass<RealmObject>)
    
        RealmConfiguration.Builder(realmObjects)
            .deleteRealmIfMigrationNeeded()
            .name("main")
            .build()
    }
    
    private val mainRealm by lazy {
        Realm.open(config)
    }
    
    val realm get() = mainRealm
}