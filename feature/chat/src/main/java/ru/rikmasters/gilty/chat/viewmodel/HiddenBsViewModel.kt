package ru.rikmasters.gilty.chat.viewmodel

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
    
    private val _images = MutableStateFlow(emptyList<AvatarModel>())
    val images = _images.asStateFlow()
    
    private val _selected = MutableStateFlow(emptyList<AvatarModel>())
    val selected = _selected.asStateFlow()
    
    suspend fun sendImages(chatId: String) {
        selected.value.forEach {
            chatVm.onSendMessage(
                chatId, attachment = listOf(it)
            )
        }
    }
    
    suspend fun selectImage(path: String) {
        val list = selected.value
        images.value.firstOrNull {
            it.thumbnail.url == path
        }?.let { image ->
            _selected.emit(
                if(list.contains(image))
                    list - image
                else list + image
            )
        }
    }
    
    suspend fun clearSelect() {
        _selected.emit(emptyList())
    }
    
    suspend fun getImages() = singleLoading {
        _images.emit(
            profileManager.getProfileHiddens()
        )
    }
}