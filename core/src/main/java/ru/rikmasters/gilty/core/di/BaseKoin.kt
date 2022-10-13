package ru.rikmasters.gilty.core.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.rikmasters.gilty.core.module.ModuleAnchor

fun initKoin(context: Context) {
    startKoin {
        androidLogger()

        androidContext(context)

        modules(
            ModuleAnchor.koin()
        )
    }
}