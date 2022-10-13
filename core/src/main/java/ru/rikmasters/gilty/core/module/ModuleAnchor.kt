package ru.rikmasters.gilty.core.module

import android.content.Context
import ru.rikmasters.gilty.core.log.Loggable

object ModuleAnchor: Loggable {

    private val modules = mutableSetOf<ModuleDefinition>()

    fun register(def: ModuleDefinition) {

        val moduleName = def::class.simpleName

        if(modules.add(def))
            logV("$moduleName registered")
        else
            logW("$moduleName already registered")
    }

    internal fun koin() = modules.map { it.koin() }

    fun init(context: Context) {

    }
}