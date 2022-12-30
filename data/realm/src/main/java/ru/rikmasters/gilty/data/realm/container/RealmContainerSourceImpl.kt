package ru.rikmasters.gilty.data.realm.container

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.EntityVariant
import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.data.realm.model.RealmRecordContainer
import ru.rikmasters.gilty.data.realm.RealmManager
import ru.rikmasters.gilty.data.realm.RealmSourceVariant
import ru.rikmasters.gilty.data.realm.model.RealmContainer
import ru.rikmasters.gilty.data.realm.model.RealmIdContainer
import kotlin.reflect.KClass
import kotlin.reflect.full.*

@Suppress("UNCHECKED_CAST")
internal class RealmContainerSourceImpl(
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
    
    private fun <T: DomainEntity, C: Any> RealmContainer.toDomain(clazz: KClass<C>): T =
        when(val entity = mapper.readerFor(clazz.java).readValue<C>(data)) {
            is EntityVariant<*> -> entity.domain() as T
            is DomainEntity -> entity as T
            else -> throw IllegalArgumentException("$entity(${entity::class}) не соответствует запросу")
        }
    
    private fun <T: DomainEntity, C: Any> Flow<ResultsChange<out RealmContainer>>.toDomainFlow(clazz: KClass<C>) =
        map { changes -> changes.list.map { it.toDomain<T, C>(clazz) } }
    
    private fun containerOf(id: Any?, data: Any, clazz: KClass<*>): RealmObject {
        val container = if(id == null)
            RealmRecordContainer()
        else
            RealmIdContainer().apply { this.id = id.hashCode() }
        
        return container.apply {
            this.data = data.toByteArray()
            this._class = clazz
        }
    }
    
    private fun <T: Any> KClass<T>.hasPrimaryKey(): Boolean {
        if(this.allSupertypes.contains(DomainEntity::class.starProjectedType))
            return this.declaredMemberFunctions.find { it.name == "primaryKey" }
                ?.returnType?.isMarkedNullable?.not() ?: false
        log.v("Primary key for ${this.simpleName} = false")
        return false
    }
    
    private val KClass<*>.javaName get() = java.name
    
    private fun findByIdQuery(id: Any, clazz: KClass<*>) =
        doQuery<RealmIdContainer>("$ID_FIELD == $0 && $CLASS_FIELD == $1", id, clazz.javaName)
            .first()
    
    private fun findAllQuery(clazz: KClass<*>): RealmQuery<out RealmContainer> {
        val query = "$CLASS_FIELD == $0"
        return if(clazz.hasPrimaryKey())
            doQuery<RealmIdContainer>(query, clazz.javaName)
        else
            doQuery<RealmRecordContainer>(query, clazz.javaName)
    }
    
    override suspend fun save(id: Any?, data: Any, clazz: KClass<*>) {
        doSave(containerOf(id, data, clazz))
    }
    
    override suspend fun <C: Any> saveAll(
        idGetter: (C) -> Any?,
        data: Collection<C>,
        clazz: KClass<*>
    ) {
        doSave(
            data.map {
                containerOf(idGetter.invoke(it), it, clazz)
            }
        )
    }
    
    override fun <T: DomainEntity> findById(id: Any, clazz: KClass<*>): T? {
        val container = doFindById(id, clazz) ?: return null
        return container.toDomain(clazz)
    }
    
    private fun doFindById(
        id: Any, clazz: KClass<*>
    ) = findByIdQuery(id, clazz).find()
    
    override fun <T: DomainEntity> find(clazz: KClass<*>): T? =
        findAllQuery(clazz).first().find()?.toDomain(clazz)
    
    override fun <T: DomainEntity> findAll(clazz: KClass<*>): List<T> {
        val containers = findAllQuery(clazz).find()
        
        return containers.map { it.toDomain(clazz) }
    }
    
    override suspend fun deleteById(
        id: Any, clazz: KClass<*>
    ) {
        doFindById(id, clazz)?.let { doDelete(it) }
    }
    
    override suspend fun deleteAll(clazz: KClass<*>) {
        doDelete(
            findAllQuery(clazz).find()
        )
    }
    
    override fun <T: DomainEntity> listenAll(clazz: KClass<*>): Flow<List<T>> =
        findAllQuery(clazz).asFlow().toDomainFlow(clazz)
}