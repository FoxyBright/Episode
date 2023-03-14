package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

var SelectCategory: CategoryModel? = null

class CategoryViewModel: ViewModel() {
    
    private val manager by inject<MeetingManager>()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    val addMeet by lazy { manager.addMeetFlow.state(null) }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun clearBase() {
        manager.clearBase()
    }
    
    suspend fun selectCategory(category: CategoryModel?) {
        manager.update(category = category)
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