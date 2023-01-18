package ru.rikmasters.gilty.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.categories.Category
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType

val SelectedCategory: List<Category> = listOf()

class CategoryViewModel: ViewModel() {
    
    private val regManager by inject<RegistrationManager>()
    
    private val _selected = MutableStateFlow(SelectedCategory)
    val selected = _selected.asStateFlow()
    
    private fun list(): List<Category> {
        
            return CategoriesType.list().map {
                Category(it.name, it.name, "#FFFFB800", it.name)
            }
        
//        var list = listOf<Category>()
//        coroutineScope.launch { list = regManager.getCategoriesList() }
//        return list
    }
    
    private val _categories = MutableStateFlow(list())
    val categories = _categories.asStateFlow()
    
    suspend fun selectCategory(category: Category) {
        val list = selected.value
        _selected.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
    }
    
    suspend fun sendCategories() {
    }
}