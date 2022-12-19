package ru.rikmasters.gilty.core.data.entity.interfaces

sealed interface EntityVariant<T: DomainEntity>: Entity {
    
    fun domain(): T
}