package ru.rikmasters.gilty.bottomsheet.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.app.checkGPS
import ru.rikmasters.gilty.core.app.checkLocationPermissions
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.LastRespond
import ru.rikmasters.gilty.shared.model.enumeration.MemberStateType.IS_ORGANIZER
import ru.rikmasters.gilty.shared.model.enumeration.UserGroupTypeModel
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

class MeetingBsViewModel : ViewModel() {

    private val meetManager by inject<MeetingManager>()
    private val context = getKoin().get<Context>()

    private val _meetId =
        MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val membersList by lazy {
        _meetId.flatMapLatest {
            it?.let {
                meetManager.getMeetMembers(it)
            } ?: flow { }
        }
    }

    private val _meet =
        MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()

    private val _lastResponse =
        MutableStateFlow<LastRespond?>(null)
    val lastResponse = _lastResponse.asStateFlow()

    private val _hidden =
        MutableStateFlow(false)
    val hidden = _hidden.asStateFlow()

    private val _menu =
        MutableStateFlow(false)
    val menu = _menu.asStateFlow()

    private val _meetReaction =
        MutableStateFlow(false)
    val meetReaction = _meetReaction.asStateFlow()

    private val _comment =
        MutableStateFlow("")
    val comment = _comment.asStateFlow()

    private val _distance =
        MutableStateFlow("")
    val distance = _distance.asStateFlow()

    suspend fun meetReactionDisable(state: Boolean) {
        _meetReaction.emit(state)
    }

    suspend fun getMeet(
        meetId: String,
    ) = singleLoading {
        meetManager.getDetailedMeet(meetId).on(
            success = {
                _meet.emit(it)
                if (meet.value?.memberState == IS_ORGANIZER) {

                    _meet.value?.organizer?.group

                    _lastResponse.emit(
                        LastRespond(
                            image = _meet.value?.responds?.thumbnail?.url,
                            count = _meet.value?.responds?.count!!,
                            isOnline = _meet.value?.isOnline?:false,
                            group = _meet.value?.organizer?.group?:UserGroupTypeModel.DEFAULT,
                        )
                    )
                }
                if (meet.value?.isOnline != true)
                    meet.value?.let {
                        _distance.emit("")
                    }
                _meetId.value = meetId
            },
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    suspend fun changeDistance(distance: String) {
        _distance.emit(distance)
    }

    val location =
        MutableStateFlow<Pair<Double, Double>?>(null)

    @SuppressLint("MissingPermission")
    suspend fun getLocation(activity: Activity) {
        if (!activity.checkLocationPermissions()
            || !activity.checkGPS()
        ) return
        getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener {
                coroutineScope.launch {
                    location.emit(it?.let {
                        it.latitude to it.longitude
                    })
                }
            }
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

    suspend fun respondForMeet(
        meetId: String,
    ) = singleLoading {
        meetManager.respondOfMeet(
            meetId = meetId,
            comment = comment.value
                .ifBlank { null },
            hidden = hidden.value
        ).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    suspend fun leaveMeet(
        meetId: String,
    ) = singleLoading {
        meetManager.leaveMeet(meetId).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }

    suspend fun canceledMeet(
        meetId: String,
    ) = singleLoading {
        meetManager.cancelMeet(meetId).on(
            success = {},
            loading = {},
            error = {
                context.errorToast(
                    it.serverMessage
                )
            }
        )
    }
}
