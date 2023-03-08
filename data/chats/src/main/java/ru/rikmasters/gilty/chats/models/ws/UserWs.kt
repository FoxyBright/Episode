package ru.rikmasters.gilty.chats.models.ws

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.shared.models.Thumbnail

data class UserWs(
    val id: String,
    val thumbnail: Thumbnail,
): DomainEntity {
    
    override fun primaryKey() = id
}