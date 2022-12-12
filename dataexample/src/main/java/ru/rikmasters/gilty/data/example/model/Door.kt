package ru.rikmasters.gilty.data.example.model

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class Door(
    val id: Int,
    val name: String,
    val room: String,
    val favorites: Boolean,
    val snapshot: String?,
): DomainEntity {
    
    override fun primaryKey() = id
}