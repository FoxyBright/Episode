package ru.rikmasters.gilty.data.realm

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.*
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.EntityVariant
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.log.log
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

class RealmSource(
    manager: RealmManager
): DbSource() {
    
    private val realm = manager.realm
    
    private val mapper =
        CBORMapper.builder().addModule(kotlinModule()).build()
    
    override suspend fun <T: DomainEntity> save(entity: T) {
        entity.dbOrNull()?.let { dbEntity ->
            dbEntity.realmObject()?.let {
                return save(it)
            }
            return save(null, dbEntity.defaultObject(), dbEntity::class)
        }
        return save(entity.primaryKey(), entity.defaultObject(), entity::class)
    }
    
    private suspend fun save(id: Any?, data: ByteArray, clazz: KClass<*>) {
        RealmContainer().apply {
            this.id = id.hashCode()
            this.data = data
            this._class = clazz
            save(this)
        }
    }
    
    private fun Any.defaultObject() =
        mapper.writeValueAsBytes(this)
    
    private fun <T: DbEntity<*>> T.realmObject(): RealmObject? =
        if(this is RealmObject) this else null
    
    private fun <T: DbEntity<*>> KClass<T>.isRealmObject() =
        supertypes.contains(RealmObject::class.starProjectedType)
    
    suspend fun save(realmObject: RealmObject) {
        realm.write {
            copyToRealm(realmObject)
        }
    }
    
    override suspend fun <T: DomainEntity> findById(id: Any, domainClass: KClass<T>): T? =
        findById(id, "id", domainClass)
    
    @Suppress("UNCHECKED_CAST")
    suspend fun <T: DomainEntity> findById(id: Any, idField: String, domainClass: KClass<T>): T? {
        val clazz = env.getEntitySpecs(domainClass).dbClass?.let { dbClass ->
            if(dbClass.isRealmObject())
                return findRealmObjectById<T>(id, idField, dbClass as KClass<RealmObject>)?.domain()
            else dbClass
        } ?: domainClass
        
        return findContainerById(id, clazz)
    }
    
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T: DomainEntity, C: Any> findContainerById(id: Any?, clazz: KClass<C>): T? {
        val container = realm.query<RealmContainer>(
            "id == $0 && _javaClass == $1", id?.hashCode(), clazz.java.canonicalName
        ).first().find() ?: return null
    
        return when(val entity = mapper.readerFor(clazz.java).readValue<C>(container.data)) {
            is EntityVariant<*> -> entity.domain() as T
            is DomainEntity -> entity as T
            else -> throw IllegalArgumentException("$entity(${entity::class}) не соответствует запросу")
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T: DomainEntity> findRealmObjectById(
        id: Any, idFiled: String, dbClass: KClass<RealmObject>
    ): DbEntity<T>? {
        return realm.query(dbClass, "$idFiled == $0", id.toRealmId())
            .first().find() as DbEntity<T>?
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