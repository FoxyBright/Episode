package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.Orientation
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.profile.OrientationModel

class OrientationBsViewModel(
    
    private val reqVm: RequirementsViewModel = RequirementsViewModel(),
): ViewModel() {
    
    private val manager by inject<MeetingManager>()
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _orientations = MutableStateFlow(emptyList<OrientationModel>())
    val orientations = _orientations.asStateFlow()
    
    private val _select = MutableStateFlow(Orientation)
    val select = _select.asStateFlow()
    
    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()
    
    init {
        coroutineScope.launch {
            addMeet.collectLatest {
                _online.emit(it?.isOnline ?: false)
            }
        }
    }
    
    suspend fun getOrientations() {
        val orientations = manager.getOrientations()
        _orientations.emit(orientations)
    }
    
    suspend fun selectOrientation(orientation: Int) {
        val orient = orientations.value[orientation]
        _select.emit(orient)
        reqVm.selectOrientation(orient)
    }
}