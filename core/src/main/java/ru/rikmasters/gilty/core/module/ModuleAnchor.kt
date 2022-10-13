package ru.rikmasters.gilty.core.module

import android.content.Context
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import ru.rikmasters.gilty.core.log.Loggable

object ModuleAnchor: Loggable {

    private val modules = mutableSetOf<ModuleDefinition>()

    var manager = ModuleManager()

    fun register(def: ModuleDefinition) {

        val moduleName = def::class.simpleName

        if(modules.add(def))
            logV("$moduleName registered")
        else
            logW("$moduleName already registered")
    }

    internal fun koin() = modules.map { it.koin() }

    fun init(context: Context) {
        logV("Package ${context.packageName}")
        val classes = Reflections(context.packageName).run {
            val query =
                Scanners.SubTypes.of(ModuleDefinition::class.java)
                //.asClass<ModuleDefinition>()
            get(query)
            this.getSubTypesOf(Object::class.java)
        }
        logV("Size ${classes.size}")
        classes.forEach {
            logV(" $it")
        }
    }
}