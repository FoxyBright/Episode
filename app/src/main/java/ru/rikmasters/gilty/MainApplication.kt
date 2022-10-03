package ru.rikmasters.gilty

import android.app.Application
import ru.rikmasters.base.di.startBaseKoin

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startBaseKoin(this)
    }
}