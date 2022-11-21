package ru.rikmasters.gilty.core.data.builder

import ru.rikmasters.gilty.core.data.entity.EntitySpecs
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import java.util.LinkedList
import kotlin.reflect.KClass

class EntitiesBuilder {
    
    private val entities = LinkedList<EntitySpecs<*>>()
    
    inline fun <reified T: DomainEntity> entity(
        noinline builder: EntitySpecsBuilder<T>.() -> Unit
    ) = entity(T::class, builder)
    
    fun <T: DomainEntity> entity(
        domainClass: KClass<T>,
        builder: EntitySpecsBuilder<T>.() -> Unit
    ) = buildEntity(domainClass, builder)
    
    private fun <T: DomainEntity> buildEntity(
        domainClass: KClass<T>,
        builder: EntitySpecsBuilder<T>.() -> Unit
    ) {
        entities.add(EntitySpecsBuilder(domainClass).apply(builder).build())
    }
    
    internal fun build() = entities
}