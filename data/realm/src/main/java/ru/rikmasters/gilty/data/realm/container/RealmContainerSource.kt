package ru.rikmasters.gilty.data.realm.container

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import kotlin.reflect.KClass

interface RealmContainerSource {
    
    suspend fun save(id: Any?, data: Any, clazz: KClass<*>)
    
    fun <T: DomainEntity> findById(id: Any?, clazz: KClass<*>): T?
    fun <T: DomainEntity> find(clazz: KClass<*>): T?
    fun <T: DomainEntity> findAll(clazz: KClass<*>): List<T>
    
    suspend fun deleteById(id: Any, clazz: KClass<*>)
    suspend fun deleteAll(clazz: KClass<*>)
}