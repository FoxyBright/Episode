package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class RespondsBsViewModel: ViewModel() {
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _observeGroupStates = MutableStateFlow(listOf(true, false))
    val observeGroupStates = _observeGroupStates.asStateFlow()
    
    suspend fun expandGroup(index: Int) {
        val list = arrayListOf<Boolean>()
        repeat(observeGroupStates.value.size) { list.add(it == index) }
        _observeGroupStates.emit(list)
    }
    
    suspend fun selectTab(tab: Int) {
        _tabs.emit(tab)
    }
    
}