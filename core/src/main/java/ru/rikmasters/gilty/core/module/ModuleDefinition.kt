package ru.rikmasters.gilty.core.module

import android.util.Log
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.rikmasters.gilty.core.log.Loggable

abstract class ModuleDefinition: Loggable {

    init {
        Log.d("no_tag", "WORKING")

        ModuleAnchor.register(this)
    }

    abstract fun koin(): Module

    abstract fun navigation()
}
