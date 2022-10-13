package ru.rikmasters.gilty.core

import android.content.Context
import ru.rikmasters.gilty.core.di.initKoin
import ru.rikmasters.gilty.core.module.ModuleAnchor

fun initApplication(context: Context) {
    ModuleAnchor.init(context)
    initKoin(context)
}