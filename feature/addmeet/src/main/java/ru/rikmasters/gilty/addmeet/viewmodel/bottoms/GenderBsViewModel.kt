package ru.rikmasters.gilty.addmeet.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.addmeet.viewmodel.Gender
import ru.rikmasters.gilty.addmeet.viewmodel.RequirementsViewModel
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.enumeration.GenderType

class GenderBsViewModel(
    
    private val reqVm: RequirementsViewModel = RequirementsViewModel(),
): ViewModel() {
    
    private val manager by inject<MeetingManager>()
    private val addMeet by lazy { manager.addMeetFlow }
    
    private val _genders = MutableStateFlow(emptyList<String>())
    val genders = _genders.asStateFlow()
    
    private val _select = MutableStateFlow(Gender)
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