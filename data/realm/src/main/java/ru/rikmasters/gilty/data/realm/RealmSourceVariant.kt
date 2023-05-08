package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.types.BaseRealmObject
import io.realm.kotlin.types.RealmObject
import ru.rikmasters.gilty.core.data.source.Source
import kotlin.reflect.KClass


abstract class RealmSourceVariant(
    manager: RealmManager
): Source {
    
    companion object {
        const val DEFAULT_QUERY = "TRUEPREDICATE"
        val UPDATE_POLICY = UpdatePolicy.ALL
    }
    
    private val realm = manager.realm
    
    protected suspend fun doSave(realmObject: RealmObject) {
        realm.write {
            copyToRealm(realmObject, UPDATE_POLICY)
        }
    }
    
    protected suspend fun doSave(realmObjects: Collection<RealmObject>) {
        realm.write {
            realmObjects.forEach { copyToRealm(it, UPDATE_POLICY) }
        }
    }
    
    protected inline fun <reified T: RealmObject> doQuery(
        query: String = DEFAULT_QUERY,
        vararg args: Any?
    ) = doQuery(T::class, query, *args)
    
    protected fun <T: RealmObject> doQuery(
        clazz: KClass<T>,
        query: String = DEFAULT_QUERY,
        vararg args: Any?
    ) = realm.query(clazz, query, *args)
    
    protected suspend fun <T: BaseRealmObject> doDelete(list: List<T>) {
        realm.write { list.forEach { deleteLatest(it) } }
    }
    
    protected suspend fun doDelete(obj: BaseRealmObject) {
        realm.write { deleteLatest(obj) }
    }
    
    private fun MutableRealm.deleteLatest(obj: BaseRealmObject) {
        findLatest(obj)?.let { delete(it) }
    }
    
    protected suspend fun <T: RealmObject> doDelete(clazz: KClass<T>) {
        realm.write { delete(clazz) }
    }
}