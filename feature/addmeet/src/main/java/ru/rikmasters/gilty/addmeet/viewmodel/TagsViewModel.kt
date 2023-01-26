package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class TagsViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _selected = MutableStateFlow(Tags)
    val selected = _selected.asStateFlow()
    
    private val _popular = MutableStateFlow(emptyList<String>())
    val popular = _popular.asStateFlow()
    
    private val _tags = MutableStateFlow(emptyList<String>())
    val tags = _tags.asStateFlow()
    
    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()
    
    suspend fun searchChange(text: String) {
        _search.emit(text)
        _tags.emit(meetManager.searchTags(text))
    }
    
    suspend fun searchClear() {
        _search.emit("")
        _tags.emit(meetManager.searchTags(""))
    }
    
    
    suspend fun getPopular() {
        var categoriesId =
            meetManager.getPopularTags(
                listOf(SelectCategory?.id)
            )
        categoriesId.forEach {
            if(Tags.contains(it))
                categoriesId = categoriesId - it
        }
        _popular.emit(Tags + categoriesId)
    }
    
    suspend fun selectTag(tag: String) {
        val list = selected.value
        _selected.emit(
            if(list.contains(tag))
                list - tag
            else if(list.size < 3)
                list + tag
            else list
        )
    }
    
    suspend fun addToPopular(tag: String) {
        _popular.emit(popular.value + tag)
    }
    
    suspend fun deleteTag(tag: String) {
        _selected.emit(selected.value - tag)
    }
    
    fun onSave() {
        Tags = selected.value
    }
}