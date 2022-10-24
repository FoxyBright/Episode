package ru.rikmasters.core.annotation.processor

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import ru.rikmasters.core.annotation.ListSubclasses
import java.io.BufferedWriter
import java.io.File
import java.io.Writer
import kotlin.reflect.KClass

class ListSubclassesProcessor(
    private val environment: SymbolProcessorEnvironment
): SymbolProcessor {

    init {
        environment.logger.warn("Processor init")
    }

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

        if(subclasses.isEmpty())
            environment.logger.warn("Classes with @ListSubclasses not found")

        val listed = mutableListOf<KSAnnotated>(*subclasses.keys.toTypedArray())
        subclasses.values.flatMapTo(listed) { it }

        val os = environment.codeGenerator.createNewFile( // FIXME падает если файл существует
            Dependencies(
                true,
                *listed.mapNotNull { it.containingFile }.toTypedArray()
            ),
            "ru.rikmasters.generated",
            "Listed"
        )

        val bw = BufferedWriter(os.writer())
        subclasses.forEach { (k, v) ->
            v.forEach {
                val msg = "$it is subclass of $k"
                environment.logger.warn(msg)
                bw.write(msg)
                bw.newLine()
            }
        }
        bw.close()

        return listed.filterNot { it.validate() }.toList()
    }
}