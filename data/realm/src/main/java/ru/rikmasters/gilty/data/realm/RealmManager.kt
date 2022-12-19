package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.data.realm.model.RealmIdContainer
import ru.rikmasters.gilty.data.realm.model.RealmRecordContainer
import kotlin.reflect.KClass

class RealmManager: Component {
    
    @Suppress("UNCHECKED_CAST")
    private val mainConfig by lazy {
        
        val realmObjects = env.specs
            .map { it.dbClass }
            .filterIsInstance<KClass<RealmObject>>()
            .toMutableSet()
        
        realmObjects.add(RealmIdContainer::class as KClass<RealmObject>)
        realmObjects.add(RealmRecordContainer::class as KClass<RealmObject>)
    
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