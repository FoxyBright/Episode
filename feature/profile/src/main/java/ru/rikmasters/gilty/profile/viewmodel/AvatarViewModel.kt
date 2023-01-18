package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class AvatarViewModel: ViewModel() {
    
    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()
    
    suspend fun showMenu(state: Boolean) {
        _menu.emit(state)
    }
}