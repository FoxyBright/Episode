package ru.rikmasters.gilty.profile.viewmodel

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

class CategoryViewModel : ViewModel() {

    private val meetManager by inject<MeetingManager>()
    private val profileManager by inject<ProfileManager>()

    private val context = getKoin().get<Context>()

    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<CategoryModel>())
    val categories = _categories.asStateFlow()

    private val _selected = MutableStateFlow(emptyList<CategoryModel>())
    val selected = _selected.asStateFlow()

    val phase = MutableStateFlow(0)

    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }

    suspend fun selectCategory(category: CategoryModel) {
        val list = selected.value
        _selected.emit(
            if (list.contains(category))
                list - category
            else list + category
        )

    }

    suspend fun setUserInterest(onSuccess: () -> Unit) = singleLoading {

        val newList = selected.value.toMutableList()
        selected.value.forEach { cat ->
            if (cat.children?.isNotEmpty() == true) {
                cat.children?.forEach { child: CategoryModel ->
                    if (newList.none { it.id == child.id }) {
                        newList.add(child)
                    }
                }
            }
        }
        //
        if (phase.value == 1) {
            meetManager.setUserInterest(selected.value).on(
                success = {
                    profileManager.updateUserCategories().on(
                        success = {
                            onSuccess()
                            _categories.emit(emptyList())
                            _selected.emit(emptyList())
                            phase.emit(0)
                        },
                        loading = {},
                        error = {
                            context.errorToast(
                                it.serverMessage
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
        _categories.emit(newList)
        phase.emit(1)
    }

    suspend fun getInterest() = singleLoading {
        profileManager.getUserCategories().on(
            success = { list ->
                _selected.emit(list)
                val newCategories = _categories.value.toMutableList()
                list.forEach { child->
                    if(newCategories.none{it.id == child.id}){
                        newCategories.add(child)
                    }
                }
                _categories.emit(newCategories)
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    suspend fun getCategories() = singleLoading {
        meetManager.getCategoriesList().on(
            success = {
                val categories = _categories.value.toMutableList()
                it.forEach { parent ->
                    if(parent !in categories) {
                        categories.add(parent)
                    }
                }
                _categories.emit(categories)
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
}