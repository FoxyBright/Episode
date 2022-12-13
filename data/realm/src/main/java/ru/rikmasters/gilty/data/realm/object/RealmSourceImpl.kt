package ru.rikmasters.gilty.data.realm.`object`

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
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
    
    override suspend fun save(realmObject: RealmObject) {
        doSave(realmObject)
    }
    
    override fun <T: DomainEntity> findById(
        id: Any, dbClass: KClass<RealmObject>, idFiled: String
    ): T? = (doFindById(id, dbClass, idFiled) as DbEntity<T>?)?.domain()
    
    private fun doFindById(
        id: Any, dbClass: KClass<RealmObject>, idFiled: String
    ) = doQuery(dbClass, "$idFiled == $0", id.toRealmId()).first().find()
    
    override fun <T: DomainEntity> find(clazz: KClass<RealmObject>): T? =
        (doQuery(clazz).first().find() as DbEntity<T>?)?.domain()
    
    override fun <T: DomainEntity> findAll(clazz: KClass<RealmObject>): List<T> {
        return doQuery(clazz).find().map { (it as DbEntity<T>).domain() }.toList()
    }
    
    override suspend fun deleteById(
        id: Any, clazz: KClass<RealmObject>, idField: String
    ) {
        doFindById(id, clazz, idField)?.let { doDelete(it) }
    }
    
    override suspend fun deleteAll(clazz: KClass<RealmObject>) {
        doDelete(clazz)
    }
}