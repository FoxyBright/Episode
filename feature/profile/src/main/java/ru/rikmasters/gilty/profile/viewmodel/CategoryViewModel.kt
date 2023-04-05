package ru.rikmasters.gilty.profile.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class CategoryViewModel: ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    private val _selected = MutableStateFlow(emptyList<CategoryModel>())
    val selected = _selected.asStateFlow()
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    suspend fun selectCategory(category: CategoryModel) {
        val list = selected.value
        _selected.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
    }
    
    suspend fun setUserInterest() = singleLoading {
        meetManager.setUserInterest(selected.value)
        profileManager.updateUserCategories()
    }
    
    suspend fun getInterest() = singleLoading {
        _selected.emit(
            profileManager.getUserCategories()
        )
    }
    
    suspend fun getCategories() = singleLoading {
        val categories = arrayListOf<CategoryModel>()
        meetManager.getCategoriesList().forEach { parent ->
            categories.add(parent)
            if(!parent.children.isNullOrEmpty())
                parent.children?.forEach { child ->
                    categories.add(child)
                }
        }
        _categories.emit(categories)
    }
}