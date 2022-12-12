package ru.rikmasters.gilty.data.realm.container

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.EntityVariant
import ru.rikmasters.gilty.data.realm.RealmContainer
import ru.rikmasters.gilty.data.realm.RealmManager
import ru.rikmasters.gilty.data.realm.RealmSourceVariant
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class RealmContainerSourceImpl(
    manager: RealmManager
): RealmSourceVariant(manager), RealmContainerSource {
    
    companion object {
        const val ID_FIELD = "id"
        const val CLASS_FIELD = "_javaClass"
    }
    
    private val mapper =
        CBORMapper.builder().addModule(kotlinModule()).build()
    
    private fun Any.toByteArray() =
        mapper.writeValueAsBytes(this)
    
    private fun <T: DomainEntity, C: Any> RealmContainer.map(clazz: KClass<C>): T {
        return when(val entity = mapper.readerFor(clazz.java).readValue<C>(data)) {
            is EntityVariant<*> -> entity.domain() as T
            is DomainEntity -> entity as T
            else -> throw IllegalArgumentException("$entity(${entity::class}) не соответствует запросу")
        }
    }
    
    override suspend fun save(id: Any?, data: Any, clazz: KClass<*>) {
        RealmContainer().apply {
            this.id = id.hashCode()
            this.data = data.toByteArray()
            this._class = clazz
            doSave(this)
        }
    }
    
    override fun <T: DomainEntity> findById(id: Any?, clazz: KClass<*>): T? {
        val container = doFindById(id, clazz) ?: return null
        return container.map(clazz)
    }
    
    private fun doFindById(
        id: Any?, clazz: KClass<*>
    ) = doQuery<RealmContainer>(
        "$ID_FIELD == $0 && $CLASS_FIELD == $1", id?.hashCode(), clazz.java.canonicalName
    ).first().find()
    
    override fun <T: DomainEntity> find(clazz: KClass<*>): T? =
        findById(null, clazz)
    
    override fun <T: DomainEntity> findAll(clazz: KClass<*>): List<T> {
        val containers = doQuery<RealmContainer>(
            "$CLASS_FIELD == $0", clazz.java.canonicalName
        ).find()
        
        return containers.map { it.map(clazz) }
    }
    
    override suspend fun deleteById(
        id: Any, clazz: KClass<*>
    ) {
        doFindById(id, clazz)?.let { doDelete(it) }
    }
    
    override suspend fun deleteAll(clazz: KClass<*>) {
        doDelete(
            doQuery<RealmContainer>("$CLASS_FIELD == $0", clazz).find()
        )
    }
}