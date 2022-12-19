package ru.rikmasters.gilty.core.data.source

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

suspend inline fun <reified T: DomainEntity> DbSource.findById(id: Any): T? = findById(id, T::class)
suspend inline fun <reified T: DomainEntity> DbSource.findAll(): List<T> = findAll(T::class)
suspend inline fun <reified T: DomainEntity> DbSource.find(): T? = find(T::class)


suspend inline fun <reified T: DomainEntity> DbSource.deleteById(id: Any) = deleteById(id, T::class)
suspend inline fun <reified T: DomainEntity> DbSource.deleteAll() = deleteAll(T::class)