package ru.rikmasters.gilty.data.realm.facade

import io.realm.kotlin.types.*
import kotlinx.coroutines.flow.Flow
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
        realmObject: (KClass<RealmObject>) -> R,
        otherwise: (KClass<*>) -> R
    ): R {
        val clazz = this.dbClass?.let { dbClass ->
            if(dbClass.isRealmObject())
                return realmObject(dbClass as KClass<RealmObject>)
            else dbClass
        } ?: this
        return otherwise(clazz)
    }
    
    private inline fun <T: DomainEntity, R> T.ifRealmOrElse(
        realmObject: (RealmObject) -> R,
        domain: (DomainEntity) -> R,
        otherwise: (Any) -> R
    ): R {
        dbOrNull()?.let { dbEntity ->
            dbEntity.realmObject()?.let {
                return realmObject(it)
            }
            return otherwise(dbEntity)
        }
        return domain(this)
    }
    
    private inline fun <T: DomainEntity, R> Collection<T>.ifRealmOrElse(
        realmObject: (Collection<RealmObject>) -> R,
        domain: (Collection<DomainEntity>) -> R,
        otherwise: (Collection<Any>) -> R
    ): R {
        if(isEmpty()) return otherwise(this)
        return first().ifRealmOrElse(
            { realmObject(this as List<RealmObject>) },
            { domain(this) },
            { otherwise(this) }
        )
    }
    
    override suspend fun <T: DomainEntity> save(entity: T) {
        entity.ifRealmOrElse(
            { objectSource.save(it) },
            { containerSource.save(it.primaryKey(), it, it::class) },
            { containerSource.save(null, it, it::class) }
        )
    }
    
    override suspend fun <T: DomainEntity> saveAll(entities: Collection<T>) {
        entities.ifRealmOrElse(
            { objectSource.saveAll(it) },
            { containerSource.saveAll(DomainEntity::primaryKey, it, it.first()::class) },
            { containerSource.saveAll({ null }, it, it::class) }
        )
    }
    
    override suspend fun <T: DomainEntity> findById(id: Any, domainClass: KClass<T>): T? =
        domainClass.ifRealmOrElse(
            { objectSource.findById(id, it) },
            { containerSource.findById(id, it) }
        )
    
    override suspend fun <T: DomainEntity> find(domainClass: KClass<T>): T? =
        domainClass.ifRealmOrElse(
            { objectSource.find(it) },
            { containerSource.find(it) }
        )
    
    override suspend fun <T: DomainEntity> findAll(domainClass: KClass<T>): List<T> =
        domainClass.ifRealmOrElse(
            { objectSource.findAll(it) },
            { containerSource.findAll(it) }
        )
    
    override suspend fun <T: DomainEntity> deleteById(id: Any, domainClass: KClass<T>) =
        domainClass.ifRealmOrElse(
            { objectSource.deleteById(id, it) },
            { containerSource.deleteById(id, it) }
        )
    
    override suspend fun <T: DomainEntity> deleteAll(domainClass: KClass<T>) =
        domainClass.ifRealmOrElse(
            { objectSource.deleteAll(it) },
            { containerSource.deleteAll(it) }
        )
    
    override fun <T: DomainEntity> listenAll(domainClass: KClass<T>): Flow<List<T>> =
        domainClass.ifRealmOrElse(
            { objectSource.listenAll(it) },
            { containerSource.listenAll(it) }
        )
}