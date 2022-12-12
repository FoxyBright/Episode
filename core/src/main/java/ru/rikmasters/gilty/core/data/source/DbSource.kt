package ru.rikmasters.gilty.core.data.source

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import kotlin.reflect.KClass

abstract class DbSource: Source() {
    
    abstract suspend fun <T: DomainEntity> save(entity: T)
    
    open suspend fun <T: DomainEntity> saveAll(entities: Collection<T>) =
        entities.forEach { save(it) }
    
    abstract suspend fun <T: DomainEntity> findById(id: Any, domainClass: KClass<T>): T?
    
    abstract suspend fun <T: DomainEntity> findAll(domainClass: KClass<T>): List<T>
    
    open suspend fun <T: DomainEntity> find(domainClass: KClass<T>): T? =
        findAll(domainClass).let { if(it.isNotEmpty()) it[0] else null }
    
    
    abstract suspend fun  <T: DomainEntity> deleteById(id: Any, domainClass: KClass<T>)
    abstract suspend fun <T: DomainEntity> deleteAll(domainClass: KClass<T>)
}