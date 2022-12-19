package ru.rikmasters.gilty.core.data.entity

import ru.rikmasters.gilty.core.data.entity.interfaces.*
import kotlin.reflect.KClass

data class EntitySpecs<T: DomainEntity>(
    val domainClass: KClass<T>,
    val dbClass: KClass<DbEntity<T>>?,
    val webClass: KClass<WebEntity<T>>?,
) {
    // TODO CacheSpecs
    // TODO WebSpecs
    // TODO DbSpecs
}