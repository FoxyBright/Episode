package ru.rikmasters.gilty.mainscreen.viewmodels.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel

class CalendarBsViewModel(
    
    private val mainVm: MainViewModel = MainViewModel(),
): ViewModel() {
    
    private val _days = MutableStateFlow(mainVm.days.value)
    val days = _days.asStateFlow()
    
    suspend fun selectDay(day: String) {
        val list = days.value
        _days.emit(
            if(list.contains(day))
                list - day
            else list + day
        )
    }
    
    suspend fun onClear() {
        _days.emit(emptyList())
    }
    
    suspend fun onSave() {
        mainVm.selectDays(days.value)
    }
}