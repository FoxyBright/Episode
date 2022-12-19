package ru.rikmasters.gilty.core.common

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.rikmasters.gilty.core.env.Environment
import ru.rikmasters.gilty.core.log.Loggable

interface Component: KoinComponent, Loggable {
    val env get() = get<Environment>()
}