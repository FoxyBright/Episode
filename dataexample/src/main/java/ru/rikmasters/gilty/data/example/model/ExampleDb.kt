package ru.rikmasters.gilty.data.example.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import ru.rikmasters.gilty.core.data.entity.interfaces.DbEntity
import java.util.UUID

// Обычный (не data) класс
class ExampleDb(
    
    // Аннотация не обязательна, объект может не иметь первичного ключа вовсе
    // Если поле первичного ключа называется id, то можно использовать RealmSource.findById(Any)
    // Иначе нужно использовать RealmSource.findById(Any, String)
    @PrimaryKey
    var id: RealmUUID,
    
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
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        
        other as ExampleDb
        
        if(id != other.id) return false
        if(name != other.name) return false
        if(age != other.age) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (age ?: 0)
        return result
    }
}