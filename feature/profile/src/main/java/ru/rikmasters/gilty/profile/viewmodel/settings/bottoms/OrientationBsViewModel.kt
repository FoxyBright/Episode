package ru.rikmasters.gilty.profile.viewmodel.settings.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class OrientationBsViewModel(
    
    private val settingVm: SettingsViewModel = SettingsViewModel(),
): ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _orientations =
        MutableStateFlow<List<OrientationModel>?>(null)
    val orientations = _orientations.asStateFlow()
    
    private val _selected = MutableStateFlow<Int?>(null)
    val selected = _selected.asStateFlow()
    
    suspend fun getOrientations() = singleLoading {
        _orientations.emit(meetManager.getOrientations())
        _selected.emit(
            orientations.value
                ?.indexOf(settingVm.orientation.value)
        )
    }
    
    suspend fun selectOrientation(orientation: Int) {
        orientations.value?.let {
            settingVm.changeOrientation(it[orientation])
        }
        _selected.emit(orientation)
    }
}