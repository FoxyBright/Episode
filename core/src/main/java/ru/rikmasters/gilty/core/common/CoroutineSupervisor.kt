package ru.rikmasters.gilty.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class CoroutineSupervisor {
    
    open val dispatcher = Dispatchers.Default
    
    open val job = SupervisorJob()
    
    val context by lazy { job + dispatcher }
    
    val scope by lazy { CoroutineScope(context) }
}