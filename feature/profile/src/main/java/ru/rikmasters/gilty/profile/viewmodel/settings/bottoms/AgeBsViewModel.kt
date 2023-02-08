package ru.rikmasters.gilty.profile.viewmodel.settings.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel

class AgeBsViewModel(
    
    private val settingVm: SettingsViewModel = SettingsViewModel(),
): ViewModel() {
    
    val ageRange = 18..99
    
    private val _age = MutableStateFlow(settingVm.age.value.toInt())
    val age = _age.asStateFlow()
    
    suspend fun changeAge(age: Int) {
        _age.emit(age)
    }
    
    suspend fun onSave() = single {
        settingVm.changeAge(age.value)
    }
}