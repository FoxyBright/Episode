package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.meeting.TagModel

var Description: String = ""
var HideAddress: Boolean = false

var Address: String = ""
var Place: String = ""
var Tags: List<TagModel> = emptyList()
var Date: String = ""
var Duration: String = ""

class DetailedViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    val addMeet by lazy { manager.addMeetFlow.state(null) }
    
    private fun getDate() = try {
        if(todayControl(Date))
            "Сегодня, ${Date.format("HH:mm")}"
        else Date.format("dd MMMM, HH:mm")
    } catch(e: Exception) {
        ""
    }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _date = MutableStateFlow(getDate())
    val date = _date.asStateFlow()

    suspend fun deleteTag(
        list: List<TagModel>, tag: TagModel,
    ) {
        manager.update(tags = (list - tag))
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changeDate(date: String) {
        manager.update(dateTime = date)
    }
    
    suspend fun changeDuration(duration: String) {
        manager.update(duration = duration)
    }
    
    suspend fun hideMeetPlace(state: Boolean) {
        manager.update(hide = state)
    }
    
    suspend fun changeDescription(text: String) {
        manager.update(description = text)
    }
    
    suspend fun clearDescription() {
        manager.update(description = "")
    }
}