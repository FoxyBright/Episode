package ru.rikmasters.gilty.profile.viewmodel.settings.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class IconsBsViewModel: ViewModel() {
    
    private val _selected = MutableStateFlow(0)
    val selected = _selected.asStateFlow()
    
    suspend fun selectIcon(value: Int) {
        _selected.emit(value)
    }
}