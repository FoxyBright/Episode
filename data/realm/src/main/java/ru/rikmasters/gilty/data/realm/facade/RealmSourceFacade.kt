package ru.rikmasters.gilty.data.realm.facade

import io.realm.kotlin.types.*
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.realm.container.RealmContainerSource
import ru.rikmasters.gilty.data.realm.`object`.RealmSource
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

@Suppress("UNCHECKED_CAST")
class RealmSourceFacade(
    private val containerSource: RealmContainerSource,
    private val objectSource: RealmSource,
): DbSource() {
    
    private val <T: DomainEntity> KClass<T>.dbClass: KClass<DbEntity<T>>?
        get() = env.getEntitySpecs(this).dbClass
    
    private fun <T: DbEntity<*>> T.realmObject(): RealmObject? =
        if(this is RealmObject) this else null
    
    private fun <T: DbEntity<*>> KClass<T>.isRealmObject() =
        supertypes.contains(RealmObject::class.starProjectedType)
    
    private inline fun <T: DomainEntity, R> KClass<T>.ifRealmOrElse(
        realmObject: (KClass<DbEntity<T>>) -> R,
        otherwise: (KClass<*>) -> R
    ): R {
        val clazz = this.dbClass?.let { dbClass ->
            if(dbClass.isRealmObject())
                return realmObject(dbClass)
            else dbClass
        } ?: this
        return otherwise(clazz)
    }
    
    override suspend fun <T: DomainEntity> save(entity: T) {
        entity.dbOrNull()?.let { dbEntity ->
            dbEntity.realmObject()?.let {
                return objectSource.save(it)
            }
            return containerSource.save(null, dbEntity, dbEntity::class)
        }
        return containerSource.save(entity.primaryKey(), entity, entity::class)
    }
    
    override suspend fun <T: DomainEntity> findById(id: Any, domainClass: KClass<T>): T? =
        domainClass.ifRealmOrElse(
            { objectSource.findById(id, it as KClass<RealmObject>) },
            { containerSource.findById(id, it) }
        )
    
    override suspend fun <T: DomainEntity> find(domainClass: KClass<T>): T? =
        domainClass.ifRealmOrElse(
            { objectSource.find(it as KClass<RealmObject>) },
            { containerSource.find(it) }
        )
    
    override suspend fun <T: DomainEntity> findAll(domainClass: KClass<T>): List<T> =
        domainClass.ifRealmOrElse(
            { objectSource.findAll(it as KClass<RealmObject>) },
            { containerSource.findAll(it) }
        )
    
    override suspend fun <T: DomainEntity> deleteById(id: Any, domainClass: KClass<T>) =
        domainClass.ifRealmOrElse(
            { objectSource.deleteById(id, it as KClass<RealmObject>) },
            { containerSource.deleteById(id, it) }
        )
    
    override suspend fun <T: DomainEntity> deleteAll(domainClass: KClass<T>) =
        domainClass.ifRealmOrElse(
            { objectSource.deleteAll(it as KClass<RealmObject>) },
            { containerSource.deleteAll(it) }
        )
}