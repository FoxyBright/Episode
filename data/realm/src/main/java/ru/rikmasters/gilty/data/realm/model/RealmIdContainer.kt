package ru.rikmasters.gilty.data.realm.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import ru.rikmasters.gilty.core.util.extension.toHex

internal class RealmIdContainer: RealmContainer(), RealmObject {
    
    @PrimaryKey
    var id: Int = 0
    
    override var data: ByteArray = byteArrayOf()
    
    override var _javaClass: String = ""
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        
        other as RealmIdContainer
        
        if(id != other.id) return false
        if(!data.contentEquals(other.data)) return false
        if(_javaClass != other._javaClass) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id
        result = 31 * result + data.contentHashCode()
        result = 31 * result + _javaClass.hashCode()
        return result
    }
    
    override fun toString(): String {
        return "RealmIdContainer(id=$id, _javaClass='$_javaClass', data=${data.toHex()})"
    }
}