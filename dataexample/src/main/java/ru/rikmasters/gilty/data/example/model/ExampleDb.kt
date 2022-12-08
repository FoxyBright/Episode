package ru.rikmasters.gilty.data.example.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import java.util.UUID

// Обычный (не data) класс
class ExampleDb(
    
    @PrimaryKey // Аннотация всегда у id
    override var id: RealmUUID,
    
    // Все поля - var
    var name: String,
    
    var age: Int?
    
    // Поддерживает ещё
    // Для связей:
    //  RealmObject
    //  RealmEmbeddedObject
    //  RealmList/RealmSet
    // Время:
    //  RealmTimestamp
    
    // Оба родителя обязательны
): DbEntity<ExampleModel>, RealmObject {
    
    // Нужен конструктор без аргументов
    // (конструктор с аргументами по умолчанию не подходит)
    constructor(): this(
        RealmUUID.random(), "", 0
    )
    
    override fun domain() =
        ExampleModel(UUID.fromString(id.toString()), name, age)
    
    // Не обязательно, но желательно
    // (генерирует среда)
    override fun toString(): String {
        return "ExampleDb(id=$id, name='$name', age=$age)"
    }
}