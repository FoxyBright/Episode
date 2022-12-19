package ru.rikmasters.gilty.example

import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.Strategy
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.data.example.repository.ExampleRepository

class ExampleViewModel: ViewModel() {
    
    private val repository: ExampleRepository by inject()
    
    suspend fun getDoors(forceWeb: Boolean = false) = single {
        repository.getDoors(forceWeb)
    }
    
    val doors by lazy { repository.doorsFlow().state(emptyList()) }
}