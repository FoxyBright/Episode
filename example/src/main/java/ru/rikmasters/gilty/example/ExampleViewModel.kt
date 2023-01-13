package ru.rikmasters.gilty.example

import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.data.example.repository.ExampleRepository

class ExampleViewModel: ViewModel(),
    LoadingTrait {
    
    private val repository by inject<ExampleRepository>()
    
    suspend fun getDoors(forceWeb: Boolean = false) = singleLoading {
        repository.getDoors(forceWeb)
    }
    
    val doors by lazy { repository.doorsFlow().state(emptyList()) }
}