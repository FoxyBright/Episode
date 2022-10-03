package ru.rikmasters.gilty

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.rikmasters.base.di.startBaseKoin

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startBaseKoin(this)
    }
}