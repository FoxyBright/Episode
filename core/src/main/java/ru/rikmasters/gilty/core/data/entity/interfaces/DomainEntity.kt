package ru.rikmasters.gilty.core.data.entity.interfaces

interface DomainEntity: Entity {
    fun db(): DbEntity<*> =
        throw NotImplementedError()
    
    fun dbOrNull(): DbEntity<*>? = try {
        db()
    } catch(nie: NotImplementedError) { null }
    
    fun web(): WebEntity<*> =
        throw NotImplementedError()
    
    fun webOrNull(): WebEntity<*>? = try {
        web()
    } catch(nie: NotImplementedError) { null }
}