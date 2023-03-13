package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
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
    
    private fun getDate() = try {
        if(todayControl(Date))
            "Сегодня, ${Date.format("HH:mm")}"
        else Date.format("dd MMMM, HH:mm")
    } catch(e: Exception) {
        ""
    }
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _hideAddress = MutableStateFlow(HideAddress)
    val hideAddress = _hideAddress.asStateFlow()
    
    private val _description = MutableStateFlow(Description)
    val description = _description.asStateFlow()
    
    private val address = MutableStateFlow(Address)
    private val point = MutableStateFlow(Place)
    private val _place = MutableStateFlow(
        if(address.value.isNotBlank() && point.value.isNotBlank())
            Pair(address.value, point.value)
        else null
    )
    
    val place = _place.asStateFlow()
    
    private val _date = MutableStateFlow(getDate())
    val date = _date.asStateFlow()
    
    private val _duration = MutableStateFlow(Duration)
    val duration = _duration.asStateFlow()
    
    private val _tags = MutableStateFlow(Tags)
    val tags = _tags.asStateFlow()
    suspend fun changePlace(place: Pair<String, String>) {
        _place.emit(place)
        Address = place.first
        Place = place.second
    }
    
    suspend fun deleteTag(tag: TagModel) {
        _tags.emit(tags.value - tag)
        Tags = tags.value
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changeDate(date: String) {
        _date.emit(date)
    }
    
    suspend fun changeDuration(duration: String) {
        _duration.emit(duration)
        Duration = duration
    }
    
    suspend fun hideMeetPlace() {
        _hideAddress.emit(!hideAddress.value)
        HideAddress = hideAddress.value
    }
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
        Description = text
    }
    
    suspend fun clearDescription() {
        _description.emit("")
        Description = ""
    }
}