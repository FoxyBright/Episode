package ru.rikmasters.gilty.core

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.module.FeatureDefinition

fun initApplication(context: Context, root: FeatureDefinition) {

    val env = Environment(root)

    val koin = startKoin {
        androidLogger()

        androidContext(context)

        modules(module(true) {
            singleOf<Environment> { env }
        })
    }

    env.onKoinStarted(koin)
}