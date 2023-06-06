package ru.rikmasters.gilty.login.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class CategoryViewModel: ViewModel() {
    
    
    private val regManager by inject<RegistrationManager>()
    private val meetManager by inject<MeetingManager>()
    
    private val context = getKoin().get<Context>()
    
    private val _selected =
        MutableStateFlow(emptyList<CategoryModel>())
    val selected = _selected.asStateFlow()
    
    private val _categories =
        MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()
    
    suspend fun selectCategory(category: CategoryModel) {
        val list = selected.value
        _selected.emit(
            if(list.contains(category))
                list - category
            else list + category
        )
    }
    
    suspend fun getCategories() {
        meetManager.getCategoriesList().on(
            success = {
                _categories.emit(it)
                regManager.getUserCategories().on(
                    success = { list ->
                        _selected.emit(list)
                    },
                    loading = {},
                    error = { e ->
                        context.errorToast(
                            e.serverMessage
                        )
                    }
                )
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
    
    suspend fun sendCategories() {
        meetManager.setUserInterest(
            selected.value
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
}