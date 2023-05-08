package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

class ParticipantsBsViewModel : ViewModel() {

    private val meetManager by inject<MeetingManager>()

    private val _meetId = MutableStateFlow<String?>(null)

    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val participants by lazy {
        _meetId.flatMapLatest {
            it?.let {
                meetManager.getMeetMembers(it)
            } ?: flow { }
        }
    }

    suspend fun getMeet(meetId: String) = singleLoading {
        _meet.emit(meetManager.getDetailedMeet(meetId))
        _meetId.value = meetId
    }
}
