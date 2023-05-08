package ru.rikmasters.gilty.profile.viewmodel.settings.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel
import ru.rikmasters.gilty.shared.model.enumeration.GenderType

class GenderBsViewModel(
    
    private val settingVm: SettingsViewModel = SettingsViewModel(),
): ViewModel() {
    
    private val _selected = MutableStateFlow(
        settingVm.gender.value?.ordinal
    )
    val selected = _selected.asStateFlow()
    
    suspend fun getGenders() {
        _selected.emit(settingVm.gender.value?.ordinal)
    }
    
    suspend fun selectGender(gender: Int) {
        settingVm.changeGender(GenderType.get(gender))
        _selected.emit(gender)
    }
}