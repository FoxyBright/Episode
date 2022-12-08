package ru.rikmasters.gilty.data.example.model

import io.realm.kotlin.types.RealmUUID
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import java.util.UUID

data class ExampleModel(
    
    override val id: UUID,
    
    val name: String,
    
    val age: Int?

): DomainEntity {
    
    override fun db() =
        ExampleDb(RealmUUID.from(id.toString()), name, age)
}