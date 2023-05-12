package ru.rikmasters.gilty.shared.models

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class AvatarAmount(
    val amount:Int?=null,
): DomainEntity{
    override fun primaryKey() = 1
}