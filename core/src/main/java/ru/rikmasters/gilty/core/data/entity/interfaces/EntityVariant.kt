package ru.rikmasters.gilty.core.data.entity.interfaces

sealed interface EntityVariant<T: DomainEntity>: Entity {
    
    fun map(): T
    fun map(domain: T): EntityVariant<T>
}