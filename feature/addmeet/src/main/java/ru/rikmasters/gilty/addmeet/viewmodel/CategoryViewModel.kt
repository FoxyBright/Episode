package ru.rikmasters.gilty.addmeet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class CategoryViewModel: ViewModel() {

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

    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
                _selectedCategory.emit(it?.category)
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

    suspend fun getCategories() {
        val categories = arrayListOf<CategoryModel>()
        manager.getCategoriesList().forEach { parent ->
            categories.add(parent)
        }
        _categories.emit(categories)
    }
}