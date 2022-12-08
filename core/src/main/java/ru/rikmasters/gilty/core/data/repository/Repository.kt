package ru.rikmasters.gilty.core.data.repository

import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.core.common.CoroutineSupervisor
import ru.rikmasters.gilty.core.data.source.Source
import ru.rikmasters.gilty.core.log.Loggable

abstract class Repository<T: Source>(
    
    protected open val primarySource: T
    
): CoroutineSupervisor(), Component {
    
    override val dispatcher = Dispatchers.IO
    
    protected suspend fun <T> background(block: suspend CoroutineScope.() -> T) =
        withContext(context, block)
}