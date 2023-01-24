package ru.rikmasters.gilty.profile.viewmodel.bottoms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.rikmasters.gilty.core.viewmodel.ViewModel

class ParticipantsBsViewModel(
    meetVm: MeetingBsViewModel = MeetingBsViewModel(),
): ViewModel() {
    
    private val _memberList =
        MutableStateFlow(meetVm.memberList.value)
    val memberList = _memberList.asStateFlow()
}