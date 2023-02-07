package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.Orientation
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class OrientationBsViewModel(
    
    private val reqVm: RequirementsViewModel = RequirementsViewModel(),
): ViewModel() {
    
    private val meetManager by inject<MeetingManager>()
    
    private val _orientations = MutableStateFlow(emptyList<OrientationModel>())
    val orientations = _orientations.asStateFlow()
    
    private val _select = MutableStateFlow(Orientation)
    val select = _select.asStateFlow()
    
    suspend fun getOrientations() {
        val orientations = meetManager.getOrientations()
        _orientations.emit(orientations)
    }
    
    suspend fun selectOrientation(orientation: Int) {
        val orient = orientations.value[orientation]
        _select.emit(orient)
        reqVm.selectOrientation(orient)
    }
}