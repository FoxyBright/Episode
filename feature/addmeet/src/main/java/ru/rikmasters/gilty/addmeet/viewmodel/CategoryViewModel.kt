package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.MeetingManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

var SelectCategory: CategoryModel? = null

class CategoryViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    private val _selected = MutableStateFlow(SelectCategory)
    val selected = _selected.asStateFlow()
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun selectCategory(category: CategoryModel) {
        _selected.emit(category)
        SelectCategory = selected.value
    }
    
    suspend fun getCategories() {
        val categories = arrayListOf<CategoryModel>()
        manager.getCategoriesList().forEach { parent ->
            categories.add(parent)
            if(!parent.children.isNullOrEmpty())
                parent.children!!.forEach { child ->
                    categories.add(child)
                }
        }
        _categories.emit(categories)
    }
}