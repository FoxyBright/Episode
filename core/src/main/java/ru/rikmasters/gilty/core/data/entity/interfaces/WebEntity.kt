package ru.rikmasters.gilty.core.data.entity.interfaces

interface WebEntity<T: DomainEntity>: EntityVariant<T> {
    
    override fun map(domain: T): EntityVariant<T> {
        throw NotImplementedError()
    }
}