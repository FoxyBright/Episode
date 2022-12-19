package ru.rikmasters.gilty.data.realm.`object`

import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.data.realm.`object`.RealmSourceImpl.Companion.DEFAULT_ID_FIELD
import kotlin.reflect.KClass

interface RealmSource {
    
    suspend fun save(realmObject: RealmObject)
    suspend fun saveAll(realmObjects: Collection<RealmObject>)
    
    fun <T: DomainEntity> findById(id: Any, dbClass: KClass<RealmObject>, idFiled: String = DEFAULT_ID_FIELD): T?
    fun <T: DomainEntity> find(clazz: KClass<RealmObject>): T?
    fun <T: DomainEntity> findAll(clazz: KClass<RealmObject>): List<T>
    
    suspend fun deleteById(id: Any, clazz: KClass<RealmObject>, idField: String = DEFAULT_ID_FIELD)
    suspend fun deleteAll(clazz: KClass<RealmObject>)
    
    fun <T: DomainEntity> listenAll(clazz: KClass<RealmObject>): Flow<List<T>>
}