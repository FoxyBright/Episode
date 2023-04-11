package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.extentions.distanceCalculator
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

class MeetingBsViewModel : ViewModel() {

    private val meetManager by inject<MeetingManager>()
    
    private val _meetId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val membersList by lazy {
        _meetId.flatMapLatest {
            it?.let {
                meetManager.getMeetMembers(it)
            } ?: flow { }
        }
    }

    private val _meet = MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()

    private val _lastResponse = MutableStateFlow<Pair<Int, String?>?>(null)
    val lastResponse = _lastResponse.asStateFlow()

    private val _hidden = MutableStateFlow(false)
    val hidden = _hidden.asStateFlow()

    private val _menu = MutableStateFlow(false)
    val menu = _menu.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment = _comment.asStateFlow()

    private val _distance = MutableStateFlow("")
    val distance = _distance.asStateFlow()

    suspend fun getMeet(meetId: String) {
        _meet.emit(meetManager.getDetailedMeet(meetId))
        if (meet.value?.memberState == IS_ORGANIZER) {
            _lastResponse.emit(
                _meet.value?.responds?.count!! to
                        _meet.value?.responds?.thumbnail?.url
            )
        }
        if (meet.value?.isOnline == true) meet.value?.let {
            _distance.emit(distanceCalculator(it.map()))
        }
        _meetId.value = meetId
    }

    suspend fun changeHidden(state: Boolean) {
        _hidden.emit(state)
    }

    suspend fun changeComment(text: String) {
        _comment.emit(text)
    }

    suspend fun clearComment() {
        _comment.emit("")
    }

    suspend fun menuDismiss(state: Boolean) {
        _menu.emit(state)
    }

    suspend fun respondForMeet(meetId: String) {
        meetManager.respondOfMeet(
            meetId,
            comment.value.ifBlank { null },
            hidden.value
        )
    }

    suspend fun leaveMeet(meetId: String) {
        meetManager.leaveMeet(meetId)
    }

    suspend fun canceledMeet(meetId: String) {
        meetManager.cancelMeet(meetId)
    }
}
