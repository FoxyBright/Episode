package ru.rikmasters.gilty.core.data.builder

import ru.rikmasters.gilty.core.data.entity.EntitySpecs
import ru.rikmasters.gilty.core.data.entity.interfaces.*
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class EntitySpecsBuilder<T: DomainEntity> internal constructor(
    private val domainClass: KClass<T>
){
    var dbClass: KClass<DbEntity<T>>? = null
        private set
    fun <D: DbEntity<T>> db(dbClass: KClass<D>) {
        this.dbClass = dbClass as KClass<DbEntity<T>>
    }
    
    var webClass: KClass<WebEntity<T>>? = null
        private set
    fun <W: WebEntity<T>> web(webClass: KClass<W>) {
        this.webClass = webClass as KClass<WebEntity<T>>
    }
    
    internal fun build(): EntitySpecs<T> {
        val specs = EntitySpecs(
            domainClass, dbClass, webClass
        )
        return specs
    }
}