package ru.rikmasters.gilty.core.data.repository

import kotlinx.coroutines.*
import ru.rikmasters.gilty.core.common.Component
import ru.rikmasters.gilty.core.common.CoroutineController
import ru.rikmasters.gilty.core.data.source.Source

abstract class Repository<T: Source>(
    
    protected open val primarySource: T
    
): CoroutineController(), Component {

}