package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

class TagsViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    val addMeet by lazy { manager.addMeetFlow }
    
    private val _selected = MutableStateFlow(emptyList<TagModel>())
    val selected = _selected.asStateFlow()
    
    private val _popular = MutableStateFlow(emptyList<TagModel>())
    val popular = _popular.asStateFlow()
    
    private val _tags = MutableStateFlow(emptyList<TagModel>())
    val tags = _tags.asStateFlow()
    
    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()
    
    private val _category = MutableStateFlow<CategoryModel?>(null)
    val category = _category.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
                _category.emit(it?.category)
                _selected.emit(it?.tags ?: emptyList())
            }
        }
    }
    
    suspend fun searchChange(text: String) {
        _search.emit(text)
        _tags.emit(manager.searchTags(text))
    }
    
    suspend fun searchClear() {
        _search.emit("")
        _tags.emit(manager.searchTags(""))
    }
    
    suspend fun getPopular() {
        _popular.emit(Tags + manager.getPopularTags(
            listOf(category.value?.id)
        ).filter { !Tags.contains(it) })
    }
    
    suspend fun selectTag(tag: TagModel) {
        val list = selected.value
        _selected.emit(
            if(list.contains(tag))
                list - tag
            else if(list.size < 3)
                list + tag
            else list
        )
    }
    
    suspend fun addToPopular(tag: TagModel) {
        _popular.emit(popular.value + tag)
    }
    
    suspend fun deleteTag(tag: TagModel) {
        _selected.emit(selected.value - tag)
    }
    
    suspend fun onSave(list: List<TagModel>) {
        manager.update(tags = list)
    }
}