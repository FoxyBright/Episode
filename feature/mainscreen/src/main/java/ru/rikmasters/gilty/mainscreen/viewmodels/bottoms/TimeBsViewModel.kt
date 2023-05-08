package ru.rikmasters.gilty.mainscreen.viewmodels.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.mainscreen.viewmodels.MainViewModel
import ru.rikmasters.gilty.shared.common.extentions.LocalTime
import java.util.Calendar

class TimeBsViewModel(
    private val mainVm: MainViewModel = MainViewModel(),
): ViewModel() {
    
    val selectedTime = mainVm.time.value
    
    private val _minutes = MutableStateFlow("00")
    val minutes = _minutes.asStateFlow()
    
    private val _hours = MutableStateFlow("00")
    val hours = _hours.asStateFlow()
    
    private val _time = MutableStateFlow("")
    val time = _time.asStateFlow()
    
    suspend fun setTime() {
        _time.emit("${hours.value}:${minutes.value}")
    }
    
    suspend fun setLocTime() {
        val lTime = selectedTime.let { lTime ->
            if(lTime.isNotBlank()) lTime.split(":").let {
                it.first() to it.last()
            } else LocalTime(Calendar.getInstance().time.time).let {
                "${it.hour()}" to "${it.minute()}"
            }
        }
        _hours.emit(lTime.first)
        _minutes.emit(lTime.second)
        setTime()
    }
    
    suspend fun minutesChange(minute: String) {
        _minutes.emit(
            minute
                .replace("start", "59")
                .replace("end", "00")
        )
    }
    
    suspend fun hoursChange(hour: String) {
        _hours.emit(
            hour
                .replace("start", "23")
                .replace("end", "00")
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