package ru.rikmasters.gilty.data.realm.model

import io.realm.kotlin.types.RealmObject
import ru.rikmasters.gilty.core.util.extension.toHex

internal class RealmRecordContainer: RealmContainer(), RealmObject {
    
    override var data: ByteArray = byteArrayOf()
    
    override var _javaClass: String = ""
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false
        
        other as RealmRecordContainer
        
        if(!data.contentEquals(other.data)) return false
        if(_javaClass != other._javaClass) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + _javaClass.hashCode()
        return result
    }
    
    override fun toString(): String {
        return "RealmRecordContainer(_javaClass='$_javaClass', data=${data.toHex()})"
    }
}