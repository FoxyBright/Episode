package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.types.*
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.log.log
import java.time.temporal.ChronoField
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

class RealmSource(
    manager: RealmManager
): DbSource() {
    
    private val realm = manager.realm
    
    override suspend fun <T: DomainEntity> save(entity: T) {
        val dbEntity = entity.dbOrNull()
        dbEntity?.realmObject()?.let {
            return save(it)
        }
        // TODO default
    }
    
    private fun <T: DbEntity<*>> T.realmObject(): RealmObject? =
        if(this is RealmObject) this else null
    
    private fun <T: DbEntity<*>> KClass<T>.isRealmObject() =
        supertypes.contains(RealmObject::class.starProjectedType)
    
    suspend fun save(dbEntity: RealmObject) {
        realm.write {
            log.v("Saving $dbEntity...")
            copyToRealm(dbEntity)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T: DomainEntity> findById(id: Any, domainClass: KClass<T>): T? {
        val clazz = env.getEntitySpecs(domainClass).dbClass?.let { dbClass ->
            if(dbClass.isRealmObject())
                return findRealmObjectById<T>(id, dbClass as KClass<RealmObject>)?.domain()
            else dbClass
        } ?: domainClass
        // TODO default
        return null
    }
    
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T: DomainEntity> findRealmObjectById(id: Any, dbClass: KClass<RealmObject>): DbEntity<T>? {
        log.v("Looking for $dbClass of $id...")
        return realm.query(dbClass, "id == $0", id.toRealmId()).first().find() as DbEntity<T>?
    }
    
    private fun Any.toRealmId(): Any = when(this) {
        is String,
        is Byte,
        is Char,
        is Short,
        is Int,
        is Long -> this
        is UUID -> RealmUUID.from(toString())
        else -> throw IllegalArgumentException("${this::class} не может быть id Realm")
    }
    
    override suspend fun <T: DomainEntity> findAll(domainClass: KClass<T>): List<T> {
        TODO("Not yet implemented")
    }
}