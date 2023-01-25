package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.addmeet.viewmodel.Date
import ru.rikmasters.gilty.addmeet.viewmodel.DetailedViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.common.extentions.*

class TimeBsViewModel(
    
    private val detailedVm: DetailedViewModel = DetailedViewModel(),
): ViewModel() {
    
    private val _date = MutableStateFlow(TODAY_LABEL)
    val date = _date.asStateFlow()
    
    private val _hour = MutableStateFlow(TIME_START)
    val hour = _hour.asStateFlow()
    
    private val _minute = MutableStateFlow(TIME_START)
    val minute = _minute.asStateFlow()
    
    suspend fun changeHour(hour: String) {
        _hour.emit(hour)
    }
    
    suspend fun changeMinute(minute: String) {
        _minute.emit(minute)
    }
    
    suspend fun changeDate(date: String) {
        _date.emit(date)
    }
    
    suspend fun onSave() { // TODO год всегда текущий, пересмотреть логику
        val fullTime = "T${_hour.value}:${_minute.value}:00Z"
        val fullDate = if(_date.value == "Сегодня")
            LOCAL_DATE.format("yyyy-MM-dd")
        else "${LOCAL_DATE.year()}-" + _date.value.format("dd MMMM", "MM-dd")
        Date = fullDate + fullTime
        detailedVm.changeDate("${_date.value}, ${hour.value}:${minute.value}")
    }
}