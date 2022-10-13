package ru.rikmasters.gilty

import android.app.Application
import ru.rikmasters.gilty.core.initApplication

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
    }
}