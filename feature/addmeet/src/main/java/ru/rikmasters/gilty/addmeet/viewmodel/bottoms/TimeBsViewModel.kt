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
import java.util.Calendar

class TimeBsViewModel(
    private val detailedVm: DetailedViewModel = DetailedViewModel(),
): ViewModel() {
    
    private val manager by inject<MeetingManager>()
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _date = MutableStateFlow("")
    val date = _date.asStateFlow()
    
    private val _hour = MutableStateFlow("")
    val hour = _hour.asStateFlow()
    
    private val _minute = MutableStateFlow("")
    val minute = _minute.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest { add ->
                add?.dateTime?.ifBlank { null }.let { storeDate ->
                    LocalDateTime(Calendar.getInstance().time.time).let { currentDate ->
                        _date.emit(
                            (storeDate?.format(FORMAT)
                                ?: currentDate.format(FORMAT))
                                .let { "$it$ZERO_TIME" }
                        )
                        _hour.emit(
                            storeDate?.format("HH")
                                ?: "${currentDate.hour()}"
                        )
                        _minute.emit(
                            storeDate?.format("mm")
                                ?: currentDate.minute().let {
                                    val min = (it - (it % 5)) + 5
                                    if(min == 5) "05" else "$min"
                                }
                        )
                    }
                }
                _online.emit(add?.isOnline ?: false)
            }
        }
    }
    
    fun isActive() = try {
        LocalDateTime.of(
            "${
                date.value.format(FORMAT)
            }T${hour.value}:${minute.value}:00"
        ).minusMinute(offset / 60_000)
            .isAfter(LocalDateTime.nowZ())
    } catch(e: Exception) {
        true
    }
    
    suspend fun onSave() {
        detailedVm.changeDate(
            LocalDateTime.of(
                "${
                    date.value.format(FORMAT)
                }T${hour.value}:${minute.value}:00"
            ).minusMinute(offset / 60_000)
                .format(FULL_DATE_FORMAT)
        )
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
}