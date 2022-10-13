package ru.rikmasters.core.annotation.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import ru.rikmasters.core.annotation.ListSubclasses
import kotlin.reflect.KClass

class ListSubclassesProcessor(
    private val environment: SymbolProcessorEnvironment
): SymbolProcessor {

    private fun Resolver.findAnnotated(clazz: KClass<*>) =
        getSymbolsWithAnnotation(clazz.qualifiedName.toString())

    private fun Resolver.findSubclasses(clazz: KSClassDeclaration) =
        getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.OBJECT }
            .filter { it.getAllSuperTypes().contains(clazz.asStarProjectedType()) }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val subclasses = resolver
            .findAnnotated(ListSubclasses::class)
            .filterIsInstance<KSClassDeclaration>()
            .associateWith { clazz ->
                resolver
                    .findSubclasses(clazz)
                    .filter { it.classKind == ClassKind.OBJECT }
                    .toList()
            }

        subclasses.forEach { (k, v) ->
            v.forEach {
                environment.logger.warn("$it is subclass of $k")
            }
        }

        val listed = mutableListOf<KSAnnotated>(*subclasses.keys.toTypedArray())
        subclasses.values.flatMapTo(listed) { it }
        return listed.filterNot { it.validate() }.toList()
    }
}