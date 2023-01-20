package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class RespondsBsViewModel: ViewModel() {
    
    private val _respondsSelectTab = MutableStateFlow(listOf(true, false))
    val respondsSelectTab = _respondsSelectTab.asStateFlow()
    
    private val _observeGroupStates = MutableStateFlow(listOf(true, false))
    val observeGroupStates = _observeGroupStates.asStateFlow()
    
    suspend fun changeRespondsTab(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(respondsSelectTab.value.size) { list.add(it == tab) }
        _respondsSelectTab.emit(list)
    }
    
    suspend fun changeObserveGroupStates(tab: Int) {
        val list = arrayListOf<Boolean>()
        repeat(observeGroupStates.value.size) { list.add(it == tab) }
        _observeGroupStates.emit(list)
    }
    
}