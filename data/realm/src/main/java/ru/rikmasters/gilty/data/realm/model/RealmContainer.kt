package ru.rikmasters.gilty.data.realm.model

import io.realm.kotlin.types.RealmObject
import kotlin.reflect.KClass

@Suppress("PropertyName")
internal class RealmContainer: RealmObject {
    
    var id: Int? = null
    
    var data: ByteArray = byteArrayOf()
    
    private var _javaClass: String = ""
    
    var _class: KClass<*>
        get() = Class.forName(_javaClass).kotlin
        set(value) { _javaClass = value.java.canonicalName!! }
}