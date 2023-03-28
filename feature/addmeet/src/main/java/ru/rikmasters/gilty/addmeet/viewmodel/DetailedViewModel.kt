package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.format
import ru.rikmasters.gilty.shared.common.extentions.todayControl
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

var Tags: List<TagModel> = emptyList()

class DetailedViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _date = MutableStateFlow<String>("")
    val date = _date.asStateFlow()
    
    fun getDate(date:String) = try {
        if(todayControl(date))
            "Сегодня, ${date.format("HH:mm")}"
        else date.format("dd MMMM, HH:mm")
    } catch(e: Exception) {
        ""
    }
    
    private val _alert = MutableStateFlow(false)
    
    val alert = _alert.asStateFlow()
    
    private val _tags = MutableStateFlow(emptyList<TagModel>())
    val tags = _tags.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()
    
    private val _duration = MutableStateFlow("")
    val duration = _duration.asStateFlow()
    
    private val _hide = MutableStateFlow(false)
    val hide = _hide.asStateFlow()
    
    private val _place = MutableStateFlow<Pair<String, String>?>(null)
    val place = _place.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    private val _category = MutableStateFlow<CategoryModel?>(null)
    val category = _category.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest { add ->
                _date.emit(add?.dateTime ?: "")
                _tags.emit(add?.tags ?: emptyList())
                _description.emit(add?.description ?: "")
                _duration.emit(add?.duration ?: "")
                _place.emit(add?.let {
                    if(
                        it.place.isNotBlank()
                        || it.address.isNotBlank()
                    ) it.place to it.address
                    else null
                })
                _hide.emit(add?.hide ?: false)
                _online.emit(add?.isOnline ?: false)
                _category.emit(add?.category)
            }
        }
    }
    
    suspend fun deleteTag(tag: TagModel) {
        val list = tags.value - tag
        _tags.emit(list)
        manager.update(tags = list)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun changeDate(date: String) {
        _date.emit(date)
    }
    
    suspend fun changeDuration(duration: String) {
        _duration.emit(duration)
        manager.update(duration = duration)
    }
    
    suspend fun hideMeetPlace() {
        _hide.emit(!hide.value)
        manager.update(hide = hide.value)
    }
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
        manager.update(description = text)
    }
    
    suspend fun clearDescription() {
        _description.emit("")
        manager.update(description = "")
    }
}