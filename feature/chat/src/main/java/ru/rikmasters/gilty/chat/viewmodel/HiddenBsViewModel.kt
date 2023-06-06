package ru.rikmasters.gilty.chat.viewmodel

import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.shared.model.profile.AvatarModel

class HiddenBsViewModel(
    private val chatVm: ChatViewModel,
): ViewModel() {
    
    private val profileManager by inject<ProfileManager>()
    
    val images by lazy {
        profileManager.getHiddenPhotos().cachedIn(coroutineScope)
    }
    
    private val _selected =
        MutableStateFlow(emptyList<AvatarModel>())
    val selected = _selected.asStateFlow()
    
    suspend fun sendImages(
        chatId: String,
    ) = singleLoading {
        selected.value.forEach {
            chatVm.onSendMessage(
                chatId = chatId,
                attachment = listOf(it)
            )
        }
    }
    
    suspend fun selectImage(image: AvatarModel) {
        val list = selected.value
        _selected.emit(
            if(list.contains(image))
                list - image
            else list + image
        )
    }
    
    suspend fun clearSelect() {
        _selected.emit(emptyList())
    }
}
