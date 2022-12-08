package ru.rikmasters.gilty.core.data.operation

import kotlinx.coroutines.*

fun interface Operation<I, O> {
    suspend fun perform(input: I): O
}

private val operationsJob = SupervisorJob()

fun <I, O> Operation<I, O>.performWith(input: I, dispatcher: CoroutineDispatcher) =
    CoroutineScope(operationsJob + dispatcher).async { perform(input) }