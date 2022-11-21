package ru.rikmasters.gilty.data.example.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.EntityVariant
import java.util.UUID

class ExampleDb(
    
    @PrimaryKey
    override val id: RealmUUID,
    
    val name: String,
    
    val age: Int
    
): DbEntity<ExampleModel>, RealmObject {
    
    override fun map() =
        ExampleModel(UUID.fromString(id.toString()), name, age)
    
    override fun map(domain: ExampleModel) =
        ExampleDb(RealmUUID.from(domain.id.toString()), domain.name, domain.age)
}