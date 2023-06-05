package ru.rikmasters.gilty.bottomsheet.viewmodel

import android.content.Context
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.core.viewmodel.trait.PullToRefreshTrait
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.meeting.FullMeetingModel

class ParticipantsBsViewModel: ViewModel(), PullToRefreshTrait {
    
    private val meetManager by inject<MeetingManager>()
    
    private val context = getKoin().get<Context>()
    private val _meetId =
        MutableStateFlow<String?>(null)
    
    private val _meet =
        MutableStateFlow<FullMeetingModel?>(null)
    val meet = _meet.asStateFlow()
    
    private val refresh = MutableStateFlow(false)
    
    override suspend fun forceRefresh() = singleLoading {
        refresh.value = !refresh.value
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val participants by lazy {
        refresh.flatMapLatest {
            _meetId.flatMapLatest { meet ->
                meet?.let {
                    getMeet(it)
                    meetManager.getMeetMembers(it)
                } ?: flow { }
            }
        }.cachedIn(coroutineScope)
    }
    
    suspend fun getMeet(
        meetId: String,
    ) = singleLoading {
        meetManager.getDetailedMeet(
            meetId = meetId
        ).on(
            success = {
                _meet.emit(it)
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
    
    suspend fun deleteMember(
        meetId: String,
        memberId: String?,
    ) = singleLoading {
        memberId?.let { member ->
            meetManager.kickMember(
                meetId = meetId,
                userId = member
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
    }
}
