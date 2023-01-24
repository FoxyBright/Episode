package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class CategoryViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    private val _selected = MutableStateFlow<CategoryModel?>(null)
    val selected = _selected.asStateFlow()
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun selectCategory(category: CategoryModel) {
        _selected.emit(category)
    }
    
    suspend fun getCategories() {
        _categories.emit(manager.getCategoriesList())
    }
}