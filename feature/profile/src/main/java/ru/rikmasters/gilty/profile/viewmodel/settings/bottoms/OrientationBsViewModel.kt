package ru.rikmasters.gilty.profile.viewmodel.settings.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class OrientationBsViewModel(
    
    private val settingVm: SettingsViewModel = SettingsViewModel(),
): ViewModel() {
    
    private val _selected = MutableStateFlow<OrientationModel?>(null)
    val selected = _selected.asStateFlow()
    
    suspend fun setSelected(orientation: OrientationModel?) {
        _selected.emit(orientation)
    }
    
    suspend fun selectOrientation(orientation: OrientationModel?) {
        orientation?.let {
            settingVm.changeOrientation(orientation)
            _selected.emit(orientation)
        }
    }
}