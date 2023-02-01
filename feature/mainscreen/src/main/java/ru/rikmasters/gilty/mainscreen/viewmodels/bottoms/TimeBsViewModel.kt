package ru.rikmasters.gilty.mainscreen.viewmodels.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.shared.common.extentions.LOCAL_TIME

class TimeBsViewModel(
    
    private val mainVm: MainViewModel = MainViewModel(),
): ViewModel() {
    
    private val hour = LOCAL_TIME.hour().let { if(it < 10) "0$it" else "$it" }
    private val minute = LOCAL_TIME.minute().let { if(it < 10) "0$it" else "$it" }
    
    private val _minutes = MutableStateFlow(minute)
    val minutes = _minutes.asStateFlow()
    
    private val _hours = MutableStateFlow(hour)
    val hours = _hours.asStateFlow()
    
    private val _time = MutableStateFlow("")
    val time = _time.asStateFlow()
    
    private suspend fun setTime() {
        _time.emit("${hours.value}:${minutes.value}")
    }
    
    suspend fun minutesChange(minute: String) {
        _minutes.emit(minute)
        setTime()
    }
    
    suspend fun hoursChange(hour: String) {
        _hours.emit(hour)
        setTime()
    }
    
    suspend fun onSave() {
        mainVm.changeTime(time.value)
    }
    
    suspend fun onClear() {
        _minutes.emit("00")
        _hours.emit("00")
        _time.emit("")
    }
}