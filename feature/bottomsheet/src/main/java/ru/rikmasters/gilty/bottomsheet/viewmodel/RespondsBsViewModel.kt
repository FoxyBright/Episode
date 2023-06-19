package ru.rikmasters.gilty.bottomsheet.viewmodel

import android.content.Context
import androidx.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.common.errorToast
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.SENT
import ru.rikmasters.gilty.shared.model.notification.MeetWithRespondsModelWithPhotos
import ru.rikmasters.gilty.shared.model.notification.RespondWithPhotos
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.*

class RespondsBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    private val context = getKoin().get<Context>()
    
    private val _meetId =
        MutableStateFlow<String?>(null)
    
    private val _tabs =
        MutableStateFlow(0)
    val tabs =
        _tabs.asStateFlow()
    
    private val _groupsStates =
        MutableStateFlow(emptyList<Int>())
    val groupsStates =
        _groupsStates.asStateFlow()
    
    // Для случаев когда нет meetId, пагинируются встречи и фото внутри откликов
    val sentResponds by lazy {
        profileManager.getResponds(
            type = SENT
        ).map { list ->
            list.map { meet ->
                meet.map {
                    // Маппит фото внутри модели ответа в pagingData
                    profileManager.getPhotosPaging(it)
                }
            }
        }
    }

    val localSentResponds =
        MutableStateFlow(emptyList<Pair<Boolean, MeetWithRespondsModelWithPhotos>>()) // Is_Deleted, RespondWithPhotos

    suspend fun setLocalSentResponds(responds:List<MeetWithRespondsModelWithPhotos>) {
        var currentIndex = localSentResponds.value.size
        if (responds.size < currentIndex) {
            currentIndex = 0
            localSentResponds.emit(emptyList())
        }
        val newList = localSentResponds.value.toMutableList()
        for(current in  currentIndex until responds.size){
            newList.add(false to responds[current])
        }
        localSentResponds.emit(newList)
    }

    val receivedResponds by lazy {
        profileManager.getResponds(
            type = RECEIVED
        ).map { list ->
            list.map { meet ->
                meet.map {
                    // Маппит фото внутри модели ответа в pagingData
                    profileManager.getPhotosPaging(it)
                }
            }
        }
    }
    val localReceivedResponds =
        MutableStateFlow(emptyList<Pair<Boolean, MeetWithRespondsModelWithPhotos>>()) // Is_Deleted, RespondWithPhotos

    suspend fun setLocalReceivedResponds(responds:List<MeetWithRespondsModelWithPhotos>) {
        var currentIndex = localReceivedResponds.value.size
        if (responds.size < currentIndex) {
            currentIndex = 0
            localReceivedResponds.emit(emptyList())
        }
        val newList = localReceivedResponds.value.toMutableList()
        for(current in  currentIndex until responds.size){
            newList.add(false to responds[current])
        }
        localReceivedResponds.emit(newList)
    }

    // Для случаев когда есть meetId, пагинируются отклики и фото внутри них
    @OptIn(ExperimentalCoroutinesApi::class)
    val responds by lazy {
        _meetId.flatMapLatest { meet ->
            meet?.let { meeting ->
                profileManager.getMeetResponds(meeting).map { list ->
                    list.map { respond ->
                        respond.map {
                            // Маппит фото внутри модели ответа в pagingData
                            profileManager.getPhotosPaging(it)
                        }
                    }
                }
            } ?: return@flatMapLatest flow { }
        }
    }
    val localResponds =
        MutableStateFlow(emptyList<Pair<Boolean, RespondWithPhotos>>()) // Is_Deleted, RespondWithPhotos

    suspend fun setLocalResponds(responds:List<RespondWithPhotos>) {
        var currentIndex = localResponds.value.size
        if (responds.size < currentIndex) {
            currentIndex = 0
            localResponds.emit(emptyList())
        }
        val newList = localResponds.value.toMutableList()
        for(current in  currentIndex until responds.size){
            newList.add(false to responds[current])
        }
        localResponds.emit(newList)
    }


    private val _viewerState =
        MutableStateFlow(false)
    val viewerState =
        _viewerState.asStateFlow()
    
    private val _viewerImages =
        MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages =
        _viewerImages.asStateFlow()
    
    private val _viewerSelectImage =
        MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage =
        _viewerSelectImage.asStateFlow()
    
    suspend fun changePhotoViewState(
        state: Boolean,
    ) {
        _viewerState.emit(state)
    }
    
    suspend fun setPhotoViewImages(
        list: List<AvatarModel?>,
    ) {
        _viewerImages.emit(list)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
    
    suspend fun selectTab(tab: Int) {
        _tabs.value = tab
    }
    
    fun updateMeetId(meetId: String?) {
        _meetId.value = meetId
    }
    
    suspend fun selectRespondsGroup(index: Int) {
        val list = groupsStates.value
        _groupsStates.emit(
            if(list.contains(index)) {
                list - index
            } else list + index
        )
    }
    
    suspend fun cancelRespond(
        respondId: String,
    ) = singleLoading {
        profileManager
            .cancelRespond(respondId)
            .on(
                success = {},
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
    }
    
    suspend fun deleteRespond(respondId: String) =
        singleLoading {
            profileManager.deleteRespond(respondId).on(
                success = {},
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
        }
    
    suspend fun acceptRespond(respondId: String) =
        singleLoading {
            profileManager.acceptRespond(respondId).on(
                success = {
                    selectTab(1)
                },
                loading = {},
                error = {
                    context.errorToast(
                        it.serverMessage
                    )
                }
            )
        }
}
