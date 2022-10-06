package ru.rikmasters.gilty

import android.app.Application
import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.rikmasters.base.di.startBaseKoin

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startBaseKoin(this)
    }
}