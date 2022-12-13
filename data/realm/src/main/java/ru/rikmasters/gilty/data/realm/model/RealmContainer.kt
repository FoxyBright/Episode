package ru.rikmasters.gilty.data.realm.model

import io.realm.kotlin.types.RealmObject
import kotlin.reflect.KClass

@Suppress("PropertyName")
internal abstract class RealmContainer: RealmObject {
    
    abstract var data: ByteArray
    
    abstract var _javaClass: String
    
    var _class: KClass<*>
        get() = Class.forName(_javaClass).kotlin
        set(value) { _javaClass = value.java.canonicalName!! }
}