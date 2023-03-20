package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.DetailedViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.*

class TimeBsViewModel(
    
    private val detailedVm: DetailedViewModel = DetailedViewModel(),
): ViewModel() {
    
    private val manager by inject<MeetingManager>()
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _date = MutableStateFlow(TODAY_LABEL)
    val date = _date.asStateFlow()
    
    private val _hour = MutableStateFlow(LOCAL_TIME.hour().toString())
    val hour = _hour.asStateFlow()
    
    private val min = (LOCAL_TIME.minute() - (LOCAL_TIME.minute() % 5) + 5).toString()
    private val _minute = MutableStateFlow(min)
    val minute = _minute.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
            }
        }
    }
    
    suspend fun changeHour(hour: String) {
        _hour.emit(hour)
    }
    
    suspend fun changeMinute(minute: String) {
        _minute.emit(minute)
    }
    
    suspend fun changeDate(date: String) {
        _date.emit(date)
    }
    
    fun normalizeDate(
        date: String, hour: String, minute: String,
    ): String { // FIXME год всегда текущий
        val fullTime = "T$hour:$minute:00Z"
        val fullDate = if(date == "Сегодня")
            LOCAL_DATE.format("yyyy-MM-dd")
        else "${LOCAL_DATE.year()}-" + date.format("dd MMMM", "MM-dd")
        return fullDate + fullTime
    }
    
    suspend fun onSave() {
        detailedVm.changeDate(
            "${_date.value}, ${hour.value}:${minute.value}"
        )
        manager.update(
            dateTime = normalizeDate(
                _date.value, _hour.value, _minute.value
            )
        )
    }
}