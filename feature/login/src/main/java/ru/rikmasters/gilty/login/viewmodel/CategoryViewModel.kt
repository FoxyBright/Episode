package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

var SelectedCategory: List<CategoryModel> = listOf()

class CategoryViewModel: ViewModel() {
    
    
    private val meetManager by inject<MeetingManager>()
    
    private val _selected = MutableStateFlow(SelectedCategory)
    val selected = _selected.asStateFlow()
    
    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    suspend fun selectCategory(category: CategoryModel) {
        val list = selected.value
        _selected.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
        SelectedCategory = selected.value
    }
    
    suspend fun getCategories(){
        _categories.emit(meetManager.getCategoriesList())
    }
    
    fun sendCategories() {
    }
}