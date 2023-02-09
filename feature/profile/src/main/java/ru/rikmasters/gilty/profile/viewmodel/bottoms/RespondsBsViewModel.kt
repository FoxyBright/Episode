package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class RespondsBsViewModel: ViewModel() {
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _groupStates = MutableStateFlow(emptyList<Int>())
    val groupStates = _groupStates.asStateFlow()
    
    suspend fun selectRespondGroup(index: Int) {
        val list = groupStates.value
        _groupStates.emit(
            if(list.contains(index))
                list - index
            else list + index
        )
    }
    
    suspend fun selectTab(tab: Int) {
        _tabs.emit(tab)
    }
}