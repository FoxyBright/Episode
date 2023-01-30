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
    
    private val _hour = MutableStateFlow(LOCAL_TIME.hour().toString())
    val hour = _hour.asStateFlow()
    
    private val _minute = MutableStateFlow(LOCAL_TIME.minute().toString())
    val minute = _minute.asStateFlow()
    
    suspend fun changeHour(hour: String) {
        val isLast = LocalDateTime.of(
            normalizeDate(date.value, hour, minute.value)
        ).isBefore(LocalDateTime.now())
        if(!isLast) _hour.emit(hour)
    }
    
    suspend fun changeMinute(minute: String) {
        val isLast = LocalDateTime.of(
            normalizeDate(date.value, hour.value, minute)
        ).isBefore(LocalDateTime.now())
        if(!isLast) _minute.emit(minute)
    }
    
    suspend fun changeDate(date: String) {
        val isLast = LocalDate.of(
            normalizeDate(date, hour.value, minute.value)
        ).isBefore(LOCAL_DATE)
        if(!isLast) _date.emit(date)
    }
    
    private fun normalizeDate(
        date: String, hour: String, minute: String,
    ): String { // FIXME год всегда текущий
        val fullTime = "T$hour:$minute:00Z"
        val fullDate = if(date == "Сегодня")
            LOCAL_DATE.format("yyyy-MM-dd")
        else "${LOCAL_DATE.year()}-" + date.format("dd MMMM", "MM-dd")
        return fullDate + fullTime
    }
    
    suspend fun onSave() {
        Date = normalizeDate(_date.value, _hour.value, _minute.value)
        detailedVm.changeDate("${_date.value}, ${hour.value}:${minute.value}")
    }
}