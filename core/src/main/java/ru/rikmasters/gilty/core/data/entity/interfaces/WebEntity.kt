package ru.rikmasters.gilty.core.data.entity.interfaces

interface WebEntity<T: DomainEntity>: EntityVariant<T> {
    
    override fun domain(): T {
        throw NotImplementedError()
    }
}