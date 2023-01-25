package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.addmeet.viewmodel.DetailedViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.Duration
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class DurationBsViewModel(
    
    private val detailedVm: DetailedViewModel = DetailedViewModel(),
): ViewModel() {
    
    private val _duration = MutableStateFlow(Duration)
    val duration = _duration.asStateFlow()
    
    suspend fun changeDuration(duration: String) {
        _duration.emit(duration)
    }
    
    suspend fun onSave() {
        Duration = duration.value
        detailedVm.changeDuration(duration.value)
    }
}