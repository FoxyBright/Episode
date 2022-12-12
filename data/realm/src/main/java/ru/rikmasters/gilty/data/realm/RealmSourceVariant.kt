package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.Deleteable
import io.realm.kotlin.types.RealmObject
import ru.rikmasters.gilty.core.data.source.Source
import kotlin.reflect.KClass

abstract class RealmSourceVariant(
    manager: RealmManager
): Source() {
    
    companion object {
        const val DEFAULT_QUERY = "TRUEPREDICATE"
    }
    
    private val realm = manager.realm
    
    protected suspend fun doSave(realmObject: RealmObject) {
        realm.write {
            copyToRealm(realmObject)
        }
    }
    
    protected inline fun <reified T: RealmObject> doQuery(
        query: String = DEFAULT_QUERY,
        vararg args: Any?
    ) = doQuery(T::class, query, args)
    
    protected fun <T: RealmObject> doQuery(
        clazz: KClass<T>,
        query: String = DEFAULT_QUERY,
        vararg args: Any?
    ) = realm.query(clazz, query, args)
    
    protected suspend fun doDelete(deleteable: Deleteable) {
        realm.write { delete(deleteable) }
    }
    
    protected suspend fun <T: RealmObject> doDelete(clazz: KClass<T>) {
        realm.write { delete(clazz) }
    }
}