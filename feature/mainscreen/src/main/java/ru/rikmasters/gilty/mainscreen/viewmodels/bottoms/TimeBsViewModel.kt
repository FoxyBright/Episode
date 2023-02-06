package ru.rikmasters.gilty.mainscreen.viewmodels.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel

class TimeBsViewModel(
    
    private val mainVm: MainViewModel = MainViewModel(),
): ViewModel() {
    
    private val _minutes = MutableStateFlow("00")
    val minutes = _minutes.asStateFlow()
    
    private val _hours = MutableStateFlow("00")
    val hours = _hours.asStateFlow()
    
    private val _time = MutableStateFlow("")
    val time = _time.asStateFlow()
    
    suspend fun setTime() {
        _time.emit("${hours.value}:${minutes.value}")
    }
    
    suspend fun minutesChange(minute: String) {
        _minutes.emit(
            when(minute) {
                "start" -> "59"
                "end" -> "00"
                else -> minute
            }
        )
    }
    
    suspend fun hoursChange(hour: String) {
        _hours.emit(
            when(hour) {
                "start" -> "23"
                "end" -> "00"
                else -> hour
            }
        )
    }
    
    suspend fun onSave() {
        _time.emit(time.value.ifBlank { "00:00" })
        mainVm.changeTime(time.value)
    }
    
    suspend fun onClear() {
        _minutes.emit("00")
        _hours.emit("00")
        _time.emit("")
        mainVm.changeTime(time.value)
    }
}