package ru.rikmasters.gilty.bottomsheet.viewmodel

import androidx.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.SENT
import ru.rikmasters.gilty.shared.model.profile.AvatarModel
import java.util.*

class RespondsBsViewModel: ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    private val _meetId = MutableStateFlow<String?>(null)
    
    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()
    
    private val _groupsStates = MutableStateFlow(emptyList<Int>())
    val groupsStates = _groupsStates.asStateFlow()
    
    // Для случаев когда нет meetId, пагинируются встречи и фото внутри откликов
    @OptIn(ExperimentalCoroutinesApi::class)
    val meetResponds by lazy {
        _tabs.flatMapLatest { tab ->
            profileManager.getResponds(
                type = if(tab == 0) SENT
                else RECEIVED
            ).map { list ->
                list.map { meet ->
                    meet.map {
                        // Маппит фото внутри модели ответа в pagingData
                        profileManager.getPhotosPaging(it)
                    }
                }
            }
        }
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
    
    private val _viewerState = MutableStateFlow(false)
    val viewerState = _viewerState.asStateFlow()
    
    private val _viewerImages = MutableStateFlow(emptyList<AvatarModel?>())
    val viewerImages = _viewerImages.asStateFlow()
    
    private val _viewerSelectImage = MutableStateFlow<AvatarModel?>(null)
    val viewerSelectImage = _viewerSelectImage.asStateFlow()
    
    suspend fun changePhotoViewState(state: Boolean) {
        _viewerState.emit(state)
    }
    
    suspend fun setPhotoViewImages(list: List<AvatarModel?>) {
        _viewerImages.emit(list)
    }
    
    suspend fun setPhotoViewSelected(photo: AvatarModel?) {
        _viewerSelectImage.emit(photo)
    }
    
    fun selectTab(tab: Int) {
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
        profileManager.cancelRespond(respondId)
    }
    
    suspend fun deleteRespond(
        respondId: String,
    ) = singleLoading {
        profileManager.deleteRespond(respondId)
    }
    
    suspend fun acceptRespond(
        respondId: String,
    ) = singleLoading {
        profileManager.acceptRespond(respondId)
    }
}
