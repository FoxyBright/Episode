package ru.rikmasters.gilty.data.realm.`object`

import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.data.realm.RealmManager
import ru.rikmasters.gilty.data.realm.RealmSourceVariant
import java.util.UUID
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class RealmSourceImpl(
    manager: RealmManager
): RealmSourceVariant(manager), RealmSource {
    
    companion object {
        const val DEFAULT_ID_FIELD = "id"
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
    
    private fun <T: DomainEntity> RealmObject.toDomain() =
        (this as DbEntity<T>).domain()
    
    private fun <T: DomainEntity> Flow<ResultsChange<RealmObject>>.toDomainFlow() =
        map { changes -> changes.list.map { it.toDomain<T>() } }
    
    
    private fun findAllQuery(clazz: KClass<RealmObject>) =
        doQuery(clazz)
    
    
    override suspend fun save(realmObject: RealmObject) {
        doSave(realmObject)
    }
    
    override suspend fun saveAll(realmObjects: Collection<RealmObject>) {
        doSave(realmObjects)
    }
    
    override fun <T: DomainEntity> findById(
        id: Any, dbClass: KClass<RealmObject>, idFiled: String
    ): T? = doFindById(id, dbClass, idFiled)?.toDomain()
    
    private fun doFindById(
        id: Any, dbClass: KClass<RealmObject>, idFiled: String
    ) = doQuery(dbClass, "$idFiled == $0", id.toRealmId()).first().find()
    
    override fun <T: DomainEntity> find(clazz: KClass<RealmObject>): T? =
        doQuery(clazz).first().find()?.toDomain()
    
    override fun <T: DomainEntity> findAll(clazz: KClass<RealmObject>): List<T> {
        return findAllQuery(clazz).find().map { it.toDomain<T>() }.toList()
    }
    
    override suspend fun deleteById(
        id: Any, clazz: KClass<RealmObject>, idField: String
    ) {
        doFindById(id, clazz, idField)?.let { doDelete(it) }
    }
    
    override suspend fun deleteAll(clazz: KClass<RealmObject>) {
        doDelete(clazz)
    }
    
    override fun <T: DomainEntity> listenAll(clazz: KClass<RealmObject>): Flow<List<T>> =
        findAllQuery(clazz).asFlow().toDomainFlow()
}