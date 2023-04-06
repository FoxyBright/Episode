package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.DetailedViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager

class DurationBsViewModel(
    
    private val detailedVm: DetailedViewModel = DetailedViewModel(),
): ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    private val _duration = MutableStateFlow("")
    val duration = _duration.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
                _duration.emit(it?.duration ?: "")
            }
        }
    }
    
    suspend fun onSave() {
        detailedVm.changeDuration(
            duration.value.ifBlank { "20 минут" }
        )
    }
    
    suspend fun changeDuration(duration: String) {
        _duration.emit(duration)
    }
}