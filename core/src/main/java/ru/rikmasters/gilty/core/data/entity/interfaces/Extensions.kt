package ru.rikmasters.gilty.core.data.entity.interfaces

inline fun <reified T: DomainEntity> Entity.domain(): T =
    when(this) {
        is DomainEntity -> if(this is T) this else throw IllegalStateException()
        is EntityVariant<*> -> domain().let { if(it is T) it else throw IllegalStateException() }
    }

inline fun <reified T: DomainEntity> Entity.domainOrNull(): T? =
    try {
        domain()
    } catch(e: NotImplementedError) {
        null
    }