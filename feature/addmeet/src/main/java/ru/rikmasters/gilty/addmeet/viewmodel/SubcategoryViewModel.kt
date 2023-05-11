package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class SubcategoryViewModel : ViewModel() {

    private val manager by inject<MeetingManager>()

    private val addMeet by lazy { manager.addMeetFlow }

    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()

    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()

    private val _selectedCategory = MutableStateFlow<CategoryModel?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()

    private val _subcategories = MutableStateFlow(emptyList<CategoryModel>())
    val subcategories = _subcategories.asStateFlow()

    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
                _selectedCategory.emit(it?.category)

                // Get categories and subcategories if category is subcategory
                if (_selectedCategory.value?.parentId == null) {
                    _selectedCategory.value?.id?.let { it1 -> getCategories(it1) }
                }
            }
        }
    }

    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }

    suspend fun clearAddMeet() {
        manager.clearAddMeet()
    }

    suspend fun selectCategory(category: CategoryModel?) {
        manager.update(category = category)
    }

    suspend fun unselectSubcategory() {
        if (_selectedCategory.value?.parentId != null)
            manager.update(category = categories.value.first { it.id == _selectedCategory.value?.parentId })
    }

    private suspend fun getCategories(parentId:String) {
        val categories = arrayListOf<CategoryModel>()
        val subcategories = arrayListOf<CategoryModel>()
        manager.getCategoriesList().forEach { parent ->
            categories.add(parent)
            if(!parent.children.isNullOrEmpty() && parent.id == parentId) {
                parent.children?.forEach { child ->
                    subcategories.add(child.copy(parentId = parent.id))
                }
            }
        }
        _categories.emit(categories)
        _subcategories.emit(subcategories)
    }
}