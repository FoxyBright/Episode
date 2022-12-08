package ru.rikmasters.gilty.data.example.model

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class ExampleDomainOnlyModel(
    
    val name: String,
    
    val age: Int,
    
): DomainEntity {
    
    override fun primaryKey() = name
}