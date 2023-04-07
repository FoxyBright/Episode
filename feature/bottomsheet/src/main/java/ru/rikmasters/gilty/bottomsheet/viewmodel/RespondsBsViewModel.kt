package ru.rikmasters.gilty.bottomsheet.viewmodel

import androidx.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.RECEIVED
import ru.rikmasters.gilty.shared.model.enumeration.RespondType.SENT
import java.util.*

class RespondsBsViewModel : ViewModel() {

    private val profileManager by inject<ProfileManager>()

    private val _meetId = MutableStateFlow<String?>(null)

    // Обновляет пагинацию
    private val refresh = MutableStateFlow(false)

    private val _tabs = MutableStateFlow(0)
    val tabs = _tabs.asStateFlow()

    private val _groupsStates = MutableStateFlow(emptyList<Int>())
    val groupsStates = _groupsStates.asStateFlow()

    // Для случаев когда нет meetId, пагинируются встречи и фото внутри откликов
    @OptIn(ExperimentalCoroutinesApi::class)
    val meetResponds by lazy {
        _tabs.flatMapLatest {
            profileManager.getResponds(
                type = if (it == 0) SENT
                else RECEIVED
            ).map {
                it.map {
                    it.map {
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
        _meetId.flatMapLatest {
            if (it == null) {
                return@flatMapLatest flow { }
            } else {
                profileManager.getMeetResponds(it).map {
                    it.map {
                        it.map {
                            // Маппит фото внутри модели ответа в pagingData
                            profileManager.getPhotosPaging(it)
                        }
                    }
                }
            }
        }
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
            if (list.contains(index)) {
                list - index
            } else list + index
        )
    }

    suspend fun cancelRespond(
        respondId: String
    ) = singleLoading {
        profileManager.cancelRespond(respondId)
    }

    suspend fun deleteRespond(
        respondId: String
    ) = singleLoading {
        profileManager.deleteRespond(respondId)
    }

    suspend fun acceptRespond(
        respondId: String
    ) = singleLoading {
        profileManager.acceptRespond(respondId)
    }
}
