package ru.rikmasters.gilty.data.realm

import io.realm.kotlin.types.RealmObject
import kotlin.reflect.KClass

internal open class RealmContainer(): RealmObject {
    
    var id: Int? = null
    
    var data: ByteArray = byteArrayOf()
    
    private var _javaClass: String = ""
    
    var _class: KClass<*>
        get() = Class.forName(_javaClass).kotlin
        set(value) { _javaClass = value.java.canonicalName!! }
}