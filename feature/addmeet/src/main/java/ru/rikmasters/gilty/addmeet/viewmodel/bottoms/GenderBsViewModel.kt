package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.addmeet.viewmodel.Gender
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType

class GenderBsViewModel(
    
    private val reqVm: RequirementsViewModel = RequirementsViewModel(),
): ViewModel() {
    
    private val _genders = MutableStateFlow(emptyList<String>())
    val genders = _genders.asStateFlow()
    
    private val _select = MutableStateFlow(Gender)
    val select = _select.asStateFlow()
    
    suspend fun getGenders() {
        val genders =
            GenderType.fullList.map { it.value }
        _genders.emit(genders)
    }
    
    suspend fun selectGender(gender: Int) {
        _select.emit(gender)
        reqVm.selectGender(gender)
    }
}