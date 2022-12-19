package ru.rikmasters.gilty.data.example.model

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

// Классы не RealmObject сохраняются
// в реалме бинарно по первичному ключу.
//
// Поддерживает всё то, что сможет спарсить Jackson
// (предположительно - стандартные классы и другие data классы)
//
// Использованные в полях RealmObject также будут сохранены бинарно
data class ExampleDomainOnlyModel(
    
    val name: String,
    
    val age: Int,
    
): DomainEntity {
    
    override fun primaryKey() = name
}